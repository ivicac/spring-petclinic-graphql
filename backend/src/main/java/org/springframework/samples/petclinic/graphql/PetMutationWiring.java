package org.springframework.samples.petclinic.graphql;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.RuntimeWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
@Component
public class PetMutationWiring implements RuntimeWiringConfigurer {

    private final static Logger logger = LoggerFactory.getLogger(PetMutationWiring.class);

    private final PetService petService;

    public PetMutationWiring(PetService petService) {
        this.petService = petService;
    }

    @Override
    public void configure(RuntimeWiring.Builder builder) {
        builder.type("Mutation", wiring -> wiring
            .dataFetcher("addPet", this::addPet)
            .dataFetcher("updatePet", this::updatePet)
        );
    }

    private AddPetPayload addPet(DataFetchingEnvironment env) {

        Map<String, ?> input = env.getArgument("input");

        Pet pet = petService.addPet(
            (int) input.get("ownerId"),
            (int) input.get("typeId"),
            (String) input.get("name"),
            (LocalDate) input.get("birthDate")
        );

        return new AddPetPayload(pet);
    }

    private UpdatePetPayload updatePet(DataFetchingEnvironment env) {

        Map<String, ?> input = env.getArgument("input");

        Pet pet = petService.updatePet(
            (int) input.get("ownerId"),
            Optional.ofNullable((Integer) input.get("typeId")),
            (String) input.get("name"),
            (LocalDate) input.get("birthDate")
        );

        return new UpdatePetPayload(pet);
    }
}
