package lt.irmantasm.nfqtask.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.Arrays;

//@Configuration
//@EnableWebFlux
class CorsGlobal implements WebFluxConfigurer {
    
//    @Override
//    public void addCorsMappings(CorsRegistry corsRegistry) {
//        corsRegistry.addMapping("/**")
//                .allowedHeaders("Access-Control-Allow-Origin")
//                .allowedHeaders("Content-Type")
//                .allowedOrigins("http://localhost:8080")
//                .allowedOrigins("http://localhost:5050")
//                .allowedMethods(HttpMethod.GET.name(),
//                        HttpMethod.OPTIONS.name(),
//                        HttpMethod.HEAD.name(),
//                        HttpMethod.POST.name(),
//                        HttpMethod.PUT.name(),
//                        HttpMethod.DELETE.name())
//                .maxAge(3600);
//    }
}