package com.dopamine.controller;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dopamine.constants.Constant;
import com.dopamine.entity.User;
import com.dopamine.utils.MailGmailUtil;

/**
 * Servlet implementation class EmailController
 */
@WebServlet("/email-list")
public class EmailController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public EmailController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "join";  // default action
        }

        String url = "/index.jsp";        
        if (action.equals("join")) {
            url = "/index.jsp";  // the "join" page
        } else if (action.equals("add")) {
            // Get parameters from the request
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");

            // Store data in User object
            User user = new User(firstName, lastName, email);
            // UserDB.insert(user); // Uncomment if you have a database insert method
            request.setAttribute("user", user);
            
            // Send email to user
            String to = email;
            String from = Constant.USERNAME; // Use the username from your Constant class
            String subject = "Welcome to our email list";
            String body = "Dear " + firstName + ",\n\n" +
                "Thanks for joining our email list. We'll make sure to send " +
                "you announcements about new products and promotions.\n" +
                "Have a great day and thanks again!\n\n" +
                "Kelly Slivkoff\n" +
                "Mike Murach & Associates";
            boolean isBodyHTML = false; // Set to false since the body is plain text

            try {
                MailGmailUtil.SendMail(to, from, subject, body, isBodyHTML);
            } catch (MessagingException e) {
                String errorMessage = 
                    "ERROR: Unable to send email. " + 
                        "Check Tomcat logs for details.<br>" +
                    "NOTE: You may need to configure your system " + 
                        "as described in chapter 14.<br>" +
                    "ERROR MESSAGE: " + e.getMessage();
                request.setAttribute("errorMessage", errorMessage);
                this.log(
                    "Unable to send email. \n" +
                    "Here is the email you tried to send: \n" +
                    "=====================================\n" +
                    "TO: " + email + "\n" +
                    "FROM: " + from + "\n" +
                    "SUBJECT: " + subject + "\n" +
                    "\n" +
                    body + "\n\n");
            }
            url = "/thanks.jsp";
        }
        
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }
}
