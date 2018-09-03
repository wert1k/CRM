package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.FacebookMessage;

import java.util.Date;
import java.util.List;

public interface FacebookMessageServiсe extends CommonService<FacebookMessage> {

	Date findMaxDate();

	FacebookMessage addFacebookMessage(FacebookMessage facebookMessage);

	void addBatchMessages(List<FacebookMessage> clients);
}
