package com.ieeegen.web.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ieeegen.db.DB;

public class LoginServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final int SESSION_EXPIRY_TIME = 1800;
	private static final int COOKIE_EXPIRY_TIME = 1800;
	private static final String DEFAULT_NAME = "No Name";
	private static final String DEFAULT_PASSWORD = "";
	private static final String USER_AUTH_TABLE="UserAuth";
	private static final String[] USER_AUTH_COLUMNS = {"USERNAME", "PASSWORD"};
	private static final String LOGIN_PAGE_PATH = "/static/webpages/login.html";
	
	private String userNameFromDB = DEFAULT_NAME;
	private String passwordFromDB = DEFAULT_PASSWORD;
	
	private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String user = request.getParameter("uname");
		String pwd = request.getParameter("pwd");
		
		ResultSet rs = null;
		try {
			DB dbHandle = new DB();
			String criteriaForUserAuth = "where " + USER_AUTH_COLUMNS[0] + " = '" + user + "'";
			rs = dbHandle.readTable(USER_AUTH_TABLE, criteriaForUserAuth, USER_AUTH_COLUMNS[0], USER_AUTH_COLUMNS[1]);
			while(rs.next()){
				userNameFromDB = rs.getString(USER_AUTH_COLUMNS[0]);
				passwordFromDB = rs.getString(USER_AUTH_COLUMNS[1]);
			}
		
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating user credentials against database", e);
		} finally {
			try {
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Exception while closing Result set.", e);
			}
		}
		
		if(userNameFromDB.equals(user) && passwordFromDB.equals(pwd)){
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			session.setMaxInactiveInterval(SESSION_EXPIRY_TIME); // Session expiry in 30 minutes
			Cookie userName = new Cookie("user", user);
			userName.setMaxAge(COOKIE_EXPIRY_TIME);
			response.addCookie(userName);
			response.sendRedirect("welcome.jsp");
		}else{
			RequestDispatcher rd = getServletContext().getRequestDispatcher(LOGIN_PAGE_PATH);
			PrintWriter out= response.getWriter();
			out.println("<font color=red>Either user name or password is wrong.</font>");
			rd.include(request, response);
		}

	}

}
