package com.ryan.gerald.beancoin.config;

/**
 * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * * I BELEIVE THIS IS NOT BEING USED!
 * WAS TRYING TO GET PERSISTENCE PROVIDER WORKING ON HEROKU, FROM 500 ERROR.
 * TURNS OUT LOCATION OF META-INF WAS THE ISSUE. CAN PROBABLY DELETE
 */

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


/**
 * Comment out three top level annotations turns this class off from Spring perspective effectively, avoiding collision with better Spring Boot Framework.
 * This class is not necessary in Spring Boot Framework, will be removed.
 */


//@Configuration
//@ComponentScan(basePackages = "com.ryan.gerald.beancoin")
//@EnableWebMvc
@Deprecated
public class MvcConfiguration implements WebMvcConfigurer {
    // was extends WebMvcConfigurerAdapter but https://stackoverflow.com/questions/47552835/the-type-webmvcconfigureradapter-is-deprecated
    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

}
