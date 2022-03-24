package org.springframework.samples.petclinic.graphql;

import org.springframework.samples.petclinic.model.Specialty;

import java.util.List;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
public class RemoveSpecialtyPayload {

    private final List<Specialty> specialties;

    public RemoveSpecialtyPayload(List<Specialty> specialties) {
        this.specialties = specialties;
    }

    public List<Specialty> getSpecialties() {
        return specialties;
    }
}
