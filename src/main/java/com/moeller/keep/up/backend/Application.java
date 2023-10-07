package com.moeller.keep.up.backend;

import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

@SpringBootApplication
public class Application {

    /*
     * You need this main method (empty) or explicit <start-class>example.FunctionConfiguration</start-class>
     * in the POM to ensure boot plug-in makes the correct entry
     */
    public static void main(String[] args) {
        // empty unless using Custom runtime at which point it should include
        // SpringApplication.run(FunctionConfiguration.class, args);
    }


}

