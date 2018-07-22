package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.AssignSkypeCall;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignSkypeCallRepository extends CommonGenericRepository<AssignSkypeCall> {

	@Query("select sl from AssignSkypeCall sl where now() BETWEEN sl.remindBeforeOfSkypeCall AND sl.dateOfSkypeCall")
	List<AssignSkypeCall> getSkypeCallDate();
}
