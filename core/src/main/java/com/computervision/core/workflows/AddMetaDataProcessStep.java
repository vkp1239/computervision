package com.computervision.core.workflows;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.computervision.core.services.ComputerVisionAIService;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.DamUtil;

/**
 * @author Vineet_Pandey
 *
 */
@Component(service = WorkflowProcess.class, immediate = true, property = {
		"process.label" + " = Computer Vision Metadata Process",
		Constants.SERVICE_DESCRIPTION + " = Adds Metadata to Assets." })
public class AddMetaDataProcessStep implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(AddMetaDataProcessStep.class);

	@Reference
	ComputerVisionAIService computerVisionAIService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workFlowSession, MetaDataMap meteDataMap)
			throws WorkflowException {
		try {
			WorkflowData workflowData = workItem.getWorkflowData();
			if (workflowData.getPayloadType().equals("JCR_PATH")) {
				String payloadPath = workflowData.getPayload().toString();
				ResourceResolver resourceResolver = workFlowSession.adaptTo(ResourceResolver.class);
				Resource resource = resourceResolver.getResource(payloadPath);
				Asset asset = resource.adaptTo(Asset.class);
				if (DamUtil.isImage(asset)) {
					log.info("It's an image");
					String response = computerVisionAIService.getMetaData(payloadPath);
					JSONObject json = new JSONObject(response);
					JSONArray categoryArray = json.getJSONArray("categories");
					List<String> categories = new ArrayList<>();
					for (int i=0; i<categoryArray.length(); i++) {
						JSONObject category = categoryArray.getJSONObject(i);
						categories.add(category.get("name").toString());
					}
					JSONObject adult = json.getJSONObject("adult");
					Boolean isAdultContent = adult.getBoolean("isAdultContent");
					Boolean isRacyContent = adult.getBoolean("isRacyContent");
					Boolean isGoryContent = adult.getBoolean("isGoryContent");
					Resource metaDataResource = resourceResolver.getResource(payloadPath + "/jcr:content/metadata");
					ModifiableValueMap map = metaDataResource.adaptTo(ModifiableValueMap.class);
					String [] array = new String [categories.size()];
					categories.toArray(array);
					log.info("Categories are : {}", array.toString());
					map.put("dc:categories", array);
					map.put("dc:isAdultContent", isAdultContent);
					map.put("dc:isRacyContent", isRacyContent);
					map.put("dc:isGoryContent", isGoryContent);
					resourceResolver.commit();
					log.info("Metadata are set");
					
				}
			}
		} catch (Exception e) {
			log.error("Error while setting Computer Vision MetaData", e);
		}

	}

}
