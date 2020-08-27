package net.kemitix.binder.app;

import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;

@Log
@ApplicationScoped
public class BinderApp {

    public void run(String[] args) {
        log.info("Binder - Starting");

        log.info("Binder - Done.");
    }

}
