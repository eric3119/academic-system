package br.ufal.ic;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AcademicSystemConfiguration extends Configuration {
//    @NotEmpty
//    private String template;
//
//    @NotEmpty
//    private String defaultName = "Stranger";

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

//    @JsonProperty
//    public String getTemplate() {
//        return template;
//    }
//
//    @JsonProperty
//    public String getDefaultName() {
//        return defaultName;
//    }
}
