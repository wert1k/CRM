package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.repository.interfaces.ClientRepository;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.repository.jql.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/audit")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class AuditRestController {
    private static final Logger logger = LoggerFactory.getLogger(AuditRestController.class);

    private final Javers javers;
    private final ClientRepository clientRepository;

    @Autowired
    public AuditRestController(Javers javers, ClientRepository clientRepository) {
        this.javers = javers;
        this.clientRepository = clientRepository;
    }

    @RequestMapping(value = "/client/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<Change>> getPersonChanges(@PathVariable Integer id) {
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(id, Client.class);

        Changes changes = javers.findChanges(jqlQuery.build());
        System.out.println (changes.prettyPrint());
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("Printing Changes with grouping by commits and by objects :");
        (changes).groupByCommit().forEach(byCommit -> {
            System.out.println("commit " + byCommit.getCommit().getId());
            byCommit.groupByObject().forEach(byObject -> {
                System.out.println("  changes on " + byObject.getGlobalId().value() + " : ");
                byObject.get().forEach(change -> {
                    System.out.println("  - " + change);
                });
            });
        });

//        changes.sort((o1, o2) -> -1 * o1.getCommitMetadata().get().getCommitDate().compareTo(o2.getCommitMetadata().get().getCommitDate()));
        logger.info("Audit controller responded with info for client with id " + id);
        return ResponseEntity.ok(changes);
    }
}
