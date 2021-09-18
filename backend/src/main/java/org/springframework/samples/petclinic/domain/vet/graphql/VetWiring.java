package org.springframework.samples.petclinic.domain.vet.graphql;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.RuntimeWiring;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.samples.petclinic.domain.vet.Vet;
import org.springframework.samples.petclinic.domain.visit.Visit;
import org.springframework.samples.petclinic.domain.visit.VisitRepository;
import org.springframework.samples.petclinic.domain.visit.graphql.VisitConnection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VetWiring implements RuntimeWiringConfigurer {
    private final VisitRepository visitRepository;

    public VetWiring(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    public void configure(RuntimeWiring.Builder builder) {
        builder.type("Vet", wiring -> wiring.dataFetcher("visits", this::visits));
    }

    private VisitConnection visits(DataFetchingEnvironment env) {
        Vet vet = env.getSource();
        List<Visit> visitList = visitRepository.findByVetId(vet.getId());
        return new VisitConnection(visitList);
    }
}
