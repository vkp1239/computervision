package com.computervision.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Vineet_Pandey
 *
 */
@ObjectClassDefinition(name = "Computer vision Osgi Config", description = "Computer vision Osgi Configuration")
public @interface ComputerVisionOSGiConfig {

    @AttributeDefinition(name = "endpoint", description = "Computer vision endPoint URL")
    String getEndPoint();


    @AttributeDefinition(name = "apikey", description = "Computer vision API Subscription Key")
    String getApiKey();

}
