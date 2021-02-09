/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import entites.Membre;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import outils.CustomedException;
import traitements.GestionMembre;

/**
 *
 * @author Win 7
 */
@WebServlet(name = "ConnexionSerlvlet", urlPatterns = {"/connexion-valider"})
public class ConnexionSerlvlet extends HttpServlet {

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
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        String pseudo = request.getParameter("Pseudo");
        String pwd = request.getParameter("Pwd");
        
        String[] res = {"off"};
        if(request.getParameterValues("resterCo") != null){
        res = request.getParameterValues("resterCo");
        }
       
        String on = "on";
        if (getServletContext().getAttribute("gestionMembre") == null) {
            getServletContext().setAttribute("gestionMembre", new GestionMembre()); // " new GestionClient()" => GestionClient GC = new GestionClient()"
        }
        GestionMembre gtMembre = (GestionMembre) getServletContext().getAttribute("gestionMembre");

        String urlJSP = "accueil";
        System.out.println(res[0]);
        Boolean ok = true;
        try {
            Membre user = gtMembre.SeConnecter(pseudo, pwd);
            session.setAttribute("user", user);
            
            if(res[0].equals(on)){
                session.setAttribute("connexionActive", res[0]);
                String chaineAleatoire = UUID.randomUUID().toString();
                System.out.println("------------------");
                System.out.println(chaineAleatoire);
               System.out.println(user.getPseudo());
                Cookie c01 = new Cookie("ResterConnecte", chaineAleatoire);
                gtMembre.InsererChaineAleatoire(user.getPseudo(), chaineAleatoire);                     
                c01.setMaxAge(60*60*24*15);
                response.addCookie(c01);
            }
            System.out.println(user);
            session.setAttribute("connecte", "Vous êtes connecté " + user.getPseudo());
      
        } catch (CustomedException ex) {
           request.setAttribute("errLogin", ex.getMessage());
    
           HashMap<String, String> erreurs = ex.getErreurs();
           request.setAttribute("errPseudo", erreurs.get("errPseudo"));
           
           request.setAttribute("errPassword", erreurs.get("errPassword"));
           
           urlJSP = "/WEB-INF/connexion.jsp";
           ok = false;
           getServletContext().getRequestDispatcher(urlJSP).include(request, response);
           
        } catch (SQLException ex) {
              System.out.println("erreur 02 sql :" + ex.getMessage());
        }
        if(ok){
         request.getRequestDispatcher("accueil").include(request,response);
        }
        //getServletContext().getRequestDispatcher(urlJSP).include(request, response);
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
        processRequest(request, response);
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

}
