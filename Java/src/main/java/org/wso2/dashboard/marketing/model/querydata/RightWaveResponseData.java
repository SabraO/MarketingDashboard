package org.wso2.dashboard.marketing.model.querydata;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by sabra on 3/9/15.
 */
public class RightWaveResponseData {

	@JsonProperty("startdate") private String startDate;

	@JsonProperty("status") private String status;

	@JsonProperty("Api Name") private String apiName;

	@JsonProperty("output") private RightWaveOutputData output;

	@JsonProperty("enddate") private String endDate;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public RightWaveOutputData getOutput() {
		return output;
	}

	public void setOutput(RightWaveOutputData output) {
		this.output = output;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
