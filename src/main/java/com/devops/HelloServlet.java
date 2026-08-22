package com.devops;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>DevOps CI/CD</title></head>");
        out.println("<body>");
        out.println("<h1>Hello from DevOps CI/CD Pipeline!</h1>");
        out.println("<p>Java Maven Web Application</p>");
        out.println("<p>Running on Apache Tomcat 10</p>");
        out.println("</body>");
        out.println("</html>");
    }
}
