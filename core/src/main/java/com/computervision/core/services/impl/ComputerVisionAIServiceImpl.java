package com.computervision.core.services.impl;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.computervision.core.config.ComputerVisionOSGiConfig;
import com.computervision.core.services.ComputerVisionAIService;

/**
 * @author Vineet_Pandey
 *
 */
@Component (service = ComputerVisionAIService.class)
@Designate(ocd = ComputerVisionOSGiConfig.class)
public class ComputerVisionAIServiceImpl implements ComputerVisionAIService{

	private static final Logger log = LoggerFactory.getLogger(ComputerVisionAIServiceImpl.class);
	
	private String endPoint;
	
	private String apiKey;
		
    @Activate
    @Modified
    protected void activate(final ComputerVisionOSGiConfig config) {
    	endPoint = config.getEndPoint();
    	apiKey = config.getApiKey();
    }
	
	@Override
	public String getMetaData(String imageLocation) {

        HttpClient httpclient = HttpClients.createDefault();
        String metaDataResponse = "";

        try
        {
            URIBuilder builder = new URIBuilder(endPoint);

            builder.setParameter("visualFeatures", "ImageType,Faces,Adult,Categories,Color,Tags,Description,Objects,Brands");
            builder.setParameter("language", "en");
            builder.setParameter("model-version", "latest");
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", apiKey);

            // Request body
            // Change the hard coded image URL with imageLocation
            String body = "{\"url\":\"https://docs.microsoft.com/en-us/azure/cognitive-services/computer-vision/images/red-shirt-logo.jpg\"}";
            
            StringEntity reqEntity = new StringEntity(body);
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) 
            {
            	metaDataResponse = EntityUtils.toString(entity);
            }
            
            log.info("MetaData Response {}",metaDataResponse);
        }
        catch (Exception e)
        {
        	log.error("Error while fetching MetaData ",e);
        }
        return metaDataResponse;
	}

}
