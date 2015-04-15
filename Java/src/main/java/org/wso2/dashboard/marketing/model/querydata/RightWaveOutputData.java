package org.wso2.dashboard.marketing.model.querydata;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by sabra on 3/9/15.
 */
public class RightWaveOutputData {

	@JsonProperty("ROW") private int row;

	@JsonProperty("EU") private int eu;

	@JsonProperty("NA") private int na;

	@JsonProperty("Unknown") private int unclassified;

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getEu() {
		return eu;
	}

	public void setEu(int eu) {
		this.eu = eu;
	}

	public int getNa() {
		return na;
	}

	public void setNa(int na) {
		this.na = na;
	}

	public int getUnclassified() {
		return unclassified;
	}

	public void setUnclassified(int unclassified) {
		this.unclassified = unclassified;
	}

}
