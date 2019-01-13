package commands.implementations.admin;

import commands.BasicCommand;
import constants.MessageConstants;
import constants.Parameters;
import constants.PathPageConstants;
import entities.Activity;
import entities.ActivityStatus;
import entities.Tracking;
import entities.User;
import manager.ConfigManagerPages;
import org.apache.log4j.Logger;
import services.ActivityService;
import services.ServiceHelper;
import services.TrackingService;
import services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

/**
 * Description: This describes actions of registration new user.
 * <p>
 * Created by Yaroslav Bodyak on 11.12.2018.
 */
public class AddActivityToUserCommand implements BasicCommand {
    private static final Logger logger = Logger.getLogger(CreateActivityCommand.class);
    private ActivityService activityService = (ActivityService)ServiceHelper.getInstance().getService("activityService");
    private UserService userService = (UserService)ServiceHelper.getInstance().getService("userService");
    private TrackingService trackingService = (TrackingService)ServiceHelper.getInstance().getService("trackingService");

    /**
     * This method describes the adding new activities logic.
     * The method uses methods of the RequestParameterIdentifier and AdminService.
     *
     * @param request - request which will be processed.
     * @return - a page which user will be directed to.
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page;
        HttpSession session = request.getSession(false);
        String activityId = request.getParameter(Parameters.ACTIVITY_ID);
        String overviewUserId = request.getParameter(Parameters.USER_ID);
        try {
            if (activityService.isUniqueClientActivity(activityId, overviewUserId)) {
                User clientUser = userService.getUserById(overviewUserId);
                clientUser.setRequestAdd(false);
                userService.updateUser(clientUser);
                List<User> userList = userService.getAllUser();
                userService.setAttributeClientToSession(clientUser, session);
                Activity addActivityToUser = activityService.getActivityById(activityId);
                Tracking tracking = new Tracking(clientUser, addActivityToUser, ActivityStatus.NEW_ACTIVITY,
                        null, "00:00:00", 0L, 0L, 0L, false);
                trackingService.registerTracking(tracking);
                List<Tracking> trackingList = trackingService.getAllTracking();
                List<Activity> activityAdminList = activityService.getAllActivities();
                userService.setAttributeToSession(activityAdminList, trackingList, userList, session);
                page = ConfigManagerPages.getInstance().getProperty(PathPageConstants.ADMIN_PAGE_PATH_CLIENT_OVERVIEW);
                logger.info(MessageConstants.SUCCESS_ADDING_ACTIVITY);
            } else {
                request.setAttribute(Parameters.OPERATION_MESSAGE, MessageConstants.ACTIVITY_HAS_BEEN_ADDED);
                List<Activity> activityAdminList = activityService.getAllActivities();
                List<Tracking> trackingList = trackingService.getAllTracking();
                List<User> userList = userService.getAllUser();
                userService.setAttributeToSession(activityAdminList, trackingList, userList, session);
                page = ConfigManagerPages.getInstance().getProperty(PathPageConstants.ADMIN_PAGE_PATH_CLIENT_OVERVIEW);
            }
        } catch (SQLException e) {
            request.setAttribute(Parameters.ERROR_DATABASE, MessageConstants.DATABASE_ACCESS_ERROR);
            page = ConfigManagerPages.getInstance().getProperty(PathPageConstants.ERROR_PAGE_PATH);
            logger.error(MessageConstants.DATABASE_ACCESS_ERROR);
        }
        return page;
    }
}