package br.ufal.ic.resources;

import br.ufal.ic.DAO.GenericDAO;
import br.ufal.ic.model.Professor;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.AllArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("professor")
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {
    private GenericDAO<Professor> professorGenericDAO;

    @GET
    @UnitOfWork
    public Response findAll(){
        return Response.ok(professorGenericDAO.findAll("br.ufal.ic.model.Professor.findAll")).build();
    }
}