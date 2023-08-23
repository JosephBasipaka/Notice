package com.learning.hello;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import com.learning.hello.controller.Notice;
import com.learning.hello.controller.NoticeController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/Notice")
public class NoticeServlet extends HttpServlet {

    private static final long serialVersionUID = -4626049725684496527L;
	private TemplateEngine templateEngine;
    private NoticeController noticeControl;
    private JakartaServletWebApplication application;

    @Override
    public void init() throws ServletException {
        super.init();

        application = JakartaServletWebApplication.buildApplication(getServletContext());	
		final WebApplicationTemplateResolver templateResolver = 
		    new WebApplicationTemplateResolver(application);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		noticeControl = new NoticeController();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	final IWebExchange webExchange = this.application.buildExchange(request, response);
        final WebContext ctx = new WebContext(webExchange);
        List<Notice> notices = noticeControl.getAllNotices();
        request.setAttribute("notices", notices);
        templateEngine.process("Notice", ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");

        Notice notice = new Notice();
        notice.setName(name);
        notice.setPhone(phone);
        noticeControl.addNotice(notice);
        addNoticeToDB(notice.getName(),notice.getPhone());
        doGet(request, response);
    }

	private void addNoticeToDB(String name, String phone) {
			
	        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Notice", "joseph", "Jos@ph")) {
             PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Notice (name, phone) VALUES (?, ?);");
             PreparedStatement selectCountStatement = connection.prepareStatement("SELECT COUNT(*) FROM Notice;");
             PreparedStatement deleteLeastUsedStatement = connection.prepareStatement("DELETE FROM Notice ORDER BY id LIMIT 1;"); {

            insertStatement.setString(1, name);
            insertStatement.setString(2, phone);
            insertStatement.executeUpdate();

            ResultSet countResult = selectCountStatement.executeQuery();
            if (countResult.next()) {
                int totalCount = countResult.getInt(1);
                if (totalCount > 6) 
                	deleteLeastUsedStatement.executeUpdate();
            	}
	        } 
	        }catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }


}
