package org.jboss.sepro.service.ws;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.jboss.sepro.dto.User;
import org.jboss.sepro.exception.DuplicateException;
import org.jboss.sepro.service.IServiceLogger;
import org.jboss.sepro.service.IUserRegistration;
import org.jboss.sepro.stereotype.WS;

@WebService(name = UserManagement.WS_NAME, serviceName = UserManagement.WS_SERVICE_NAME, targetNamespace = UserManagement.WS_NAMESPACE)
@HandlerChain(file = "/handler-chain.xml")
public class UserManagement {

    @Inject
    @WS
    IServiceLogger slogger;
    
    public final static String WS_NAME = "UserManagement";
    public final static String WS_SERVICE_NAME = "UserManagementService";
    public final static String WS_NAMESPACE = "http://sepro.jboss.org";

    @Resource
    private WebServiceContext context;

    @Inject
    IUserRegistration userRegistration;

    @WebMethod
    public void registerUser(@WebParam(name = "user", targetNamespace = "http://sepro.jboss.org") User user)
            throws DuplicateException {
        slogger.addServiceLog(getClass().getSimpleName(), "Register user " + ((user != null)?user.toString():"null"));
        MessageContext ctx = context.getMessageContext();
        HttpServletResponse response = (HttpServletResponse) ctx.get(MessageContext.SERVLET_RESPONSE);
        try {
            userRegistration.registerUser(user);
            response.sendError(201); // CREATED
        } catch (IOException ex) {

        }
    }

    @WebMethod
    @WebResult(name = "user")
    public List<User> getList() {
        slogger.addServiceLog(getClass().getSimpleName(), "Get all users");
        return userRegistration.getAllUsers();
    }

    @WebMethod
    @WebResult(name = "user")
    public User getDetails(@WebParam(name = "username") String username) {
        slogger.addServiceLog(getClass().getSimpleName(), "Get user details for " + username);
        return userRegistration.getUser(username);
    }

    @WebMethod
    public void removeUser(@WebParam(name = "user") User user) {
        slogger.addServiceLog(getClass().getSimpleName(), "Remove user " + ((user != null)?user.toString():"null"));
        userRegistration.removeUser(user);
    }

}
