package com.bendb.example;

import com.bendb.dropwizard.jooq.JooqBundle;
import com.bendb.dropwizard.jooq.JooqFactory;
import com.bendb.example.resources.PostsResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.SortedMap;
import java.util.TreeMap;

public class ExampleApp extends Application<ExampleConfig> {
    public static void main(String[] args) throws Exception {
        new ExampleApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<ExampleConfig> bootstrap) {
        bootstrap.addBundle(new FlywayBundle<ExampleConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ExampleConfig configuration) {
                return configuration.dataSourceFactoryMaster();
            }

            @Override
            public FlywayFactory getFlywayFactory(ExampleConfig configuration) {
                return configuration.flyway();
            }
        });

        bootstrap.addBundle(new JooqBundle<ExampleConfig>() {

            /**
             * Required override to define default DataSourceFactory.
             */
            @Override
            public DataSourceFactory getDataSourceFactory(ExampleConfig configuration) {
                return configuration.dataSourceFactoryMaster();
            }

            /**
             * Optional override to define secondary DataSourceFactories.
             */
            @Override
            public SortedMap<String,DataSourceFactory> getSecondaryDataSourceFactories(ExampleConfig configuration) {
                // override this method to define database configurations
                final SortedMap<String,DataSourceFactory> dataSourceFactoryMap = new TreeMap<>();
                dataSourceFactoryMap.put("slave", configuration.dataSourceFactorySlave());
                return dataSourceFactoryMap;
            }

            /**
             * Optional override to define reference name of the default DataSourceFactory.
             * Defaults to "jooq".
             */
            @Override
            public String primaryDataSourceName() {
                return "master";
            }

            @Override
            public JooqFactory getJooqFactory(ExampleConfig configuration) {
                return configuration.jooq();
            }
        });
    }

    @Override
    public void run(ExampleConfig configuration, Environment environment) throws Exception {
        environment.jersey().register(PostsResource.class);
    }
}