/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package post;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import json.Article;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import utilities.DirectoryManager;
import utilities.Upload;

/**
 *
 * @author kealan
 * @version 1.0
 * @date 16/2/14
 */

@WebServlet(name = "EditPost", urlPatterns = {"/EditPost"})
public class EditPost extends HttpServlet {
    
    private HttpSession session;
    
    private String charityName;
    private String trimmedCharityName;
    private String servletContext;
    private String servletPath;
    
    
    private String generalSelected      = "";
    private String lostAndFoundSelected = "";
    private String sponsorshipSelected  = "";
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            
            init(request);
            if(session.getAttribute("authorised") == null) {
                response.sendRedirect("Login");
            } else {
                String idOfPostToEdit = request.getParameter("id");
                renderPostToBeEdited(request, out, idOfPostToEdit);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        LinkedHashMap formFieldMap = Upload.processMultipartForm(request, charityName, false);
        String idOfPostToUpdate = formFieldMap.get("id").toString();
        Article.updateArticleById(request, idOfPostToUpdate,formFieldMap );
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void init(HttpServletRequest request){
         
        session = request.getSession(true);
        
        //Get Charity Name from Session
        charityName = (String)session.getAttribute("charityName");

        //Trim, set to lower case and remove white spaces
        trimmedCharityName = DirectoryManager.toLowerCaseAndTrim(charityName);
        
        servletContext = request.getContextPath();
        servletPath    = request.getServletPath();
    }
    
   
    
    private void renderPostToBeEdited(HttpServletRequest request, PrintWriter out,String idOfPostToEdit){
        String id;
        String title;
        String description;
        String content;
        String img;
        String type;
        String date;
        String tags;
        
        JSONObject article =  Article.getArticleById(request, idOfPostToEdit);
        System.out.print(article);
        id          = article.get("id").toString();
        title       = article.get("title").toString();
        description = article.get("description").toString();
        content     = article.get("content").toString();
        img         = article.get("img").toString();
        type        = article.get("type").toString();
        date        = article.get("date").toString();
        tags        = Article.getTagsAsString(article);
        
        /* Just the HTML for the form, gotten with an JQuery AJAX call*/
                
        out.println("<form method='POST' action='" + servletContext + servletPath +"' enctype='multipart/form-data' >");
        out.println("<fieldset>");
        out.println("<legend>Edit Post</legend>");
        out.println("Title: <input type='text' name='title' value='" + title + "' placeholder='Post Title'/> <br />");
        out.println("<hr />");
        selectChosenType(type);
        out.println("Type: <select name='type' placeholder='Type of Post'>");
        out.println("<option value='general' "+ generalSelected +">General</option>");
        out.println("<option value='lost_and_found' "+ lostAndFoundSelected +">Lost and Found</option>");
        out.println("<option value='sponsorship' "+ sponsorshipSelected +">Sponsorship</option>");
        out.println("</select><br />");
        out.println("<hr />");
        out.println("Brief Description: <textarea name=\"description\" rows=\"5\" cols=\"10\">" + description + "</textarea><br />");
        out.println("<hr />");
        out.println("Content: <textarea name=\"content\" rows=\"15\" cols=\"30\"> " + content + "</textarea><br />");
        out.println("<hr />");
        out.println("Upload Logo Image : <input id='file' type='file' name='filename' size='50'/><br/>");

        if("".equals(img)){
            out.println("<p class=\"float\"> No Image uploaded yet!</p>");
        }else{
            out.println("<img src='charities/" + trimmedCharityName  + "/uploads/" + img + "' id='articleImg' /><br/>");
        }
        out.println("<hr />");
        out.println("Tags : <input type='text' name='tags'  value='" + tags + "'placeholder='Tags Seperated by a Space' /> <br />");
        out.println("<input type=\"hidden\" name='id' value='" + id + "'/>");
        out.println("<input type=\"hidden\" name='originalImg' value='" + img + "'/>");
        out.println("<hr />");
        out.println("<input type=\"submit\" value=\"Submit\"/>");
        out.println("<input type=\"reset\" value=\"Clear\"/>");
        out.println("</fieldset>");
        out.println("</form>");
        out.println("<p>Return to <a href=\"Dashboard\">Dashboard</a></p>");


        
           
     }
    
    
    
    private void selectChosenType(String type){
        if(type.equals("general")){
            generalSelected = "selected";
        }else if(type.equals("lost_and_found")){
            lostAndFoundSelected = "selected";
        }else if(type.equals("sponsorship")){
            sponsorshipSelected = "selected";
        }
    }
}
