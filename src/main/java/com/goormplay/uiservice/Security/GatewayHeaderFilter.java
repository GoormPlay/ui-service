 package com.goormplay.uiservice.Security;

 import jakarta.servlet.*;
 import jakarta.servlet.http.HttpServletRequest;
 import lombok.extern.slf4j.Slf4j;
 import org.springframework.http.HttpStatus;
 import org.springframework.web.client.HttpClientErrorException;

 import java.io.IOException;

 @Slf4j
 public class GatewayHeaderFilter implements Filter {
     @Override
     public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
             throws IOException, ServletException {

         HttpServletRequest request = (HttpServletRequest) req;
         String path = request.getRequestURI();
         log.info("request : 요청 들어옴");
         log.info("request URI : {}", path);

         // actuator는 예외 처리
         if (path.startsWith("/actuator")) {
             chain.doFilter(req, res);
             return;
         }

         String fromGateway = request.getHeader("X-From-Gateway");
         log.info("request fromGateway : {}", fromGateway);
         if (!"true".equals(fromGateway)) {
             throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid Request");
         }

         chain.doFilter(req, res); // 다음 필터로 요청 전달
     }
 }