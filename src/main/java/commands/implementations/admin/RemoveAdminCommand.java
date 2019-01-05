package commands.implementations.admin;

import commands.BasicCommand;
import constants.MessageConstants;
import constants.Parameters;
import constants.PathPageConstants;
import entities.Activity;
import entities.Tracking;
import entities.User;
import manager.ConfigManagerPages;
import org.apache.log4j.Logger;
import services.ActivityService;
import services.TrackingService;
import services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

public class RemoveAdminCommand implements BasicCommand {
    private static final Logger logger = Logger.getLogger(CreateActivityCommand.class);

    /**
     * This method describes the removing activities logic.
     * The method uses methods of the RequestParameterIdentifier and AdminService.
     *
     * @param request - request which will be processed.
     * @return - a page which user will be directed to.
     */
    @Override
    public String execute(HttpServletRequest request) {
        String page = null;
        HttpSession session = request.getSession(false);
        Integer trackingId = Integer.valueOf(request.getParameter(Parameters.TRACKING_ID));
        try {
            TrackingService.getInstance().deleteTrackingById(trackingId);
            List<Activity> activityAdminList = ActivityService.getInstance().getAllActivities();
            List<Tracking> trackingList = TrackingService.getInstance().getAllTracking();
            List<User> userList = UserService.getInstance().getAllUser();
            UserService.getInstance().setAttributeToSession(activityAdminList, trackingList, userList, session);
            page = ConfigManagerPages.getInstance().getProperty(PathPageConstants.ADMIN_PAGE_PATH_CLIENT_OVERVIEW);
        } catch (SQLException e) {
            page = ConfigManagerPages.getInstance().getProperty(PathPageConstants.ERROR_PAGE_PATH);
            request.setAttribute(Parameters.ERROR_DATABASE, MessageConstants.DATABASE_ACCESS_ERROR);
        }
        return page;
    }
}