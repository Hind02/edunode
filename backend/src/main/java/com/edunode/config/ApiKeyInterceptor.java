package com.edunode.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// Intercepteur pour vérifier une clé d'API simple sur POST, PUT, DELETE
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Value("${edunode.api.key}")
    private String apiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        
        // On n'exige la clé que pour les méthodes de modification
        if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
            // Dans un appel pre-flight CORS (OPTIONS), on laisse passer
            if (method.equals("OPTIONS")) {
                return true;
            }
            
            String requestApiKey = request.getHeader("X-API-KEY");
            if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or missing X-API-KEY");
                return false;
            }
        }
        return true;
    }
}
