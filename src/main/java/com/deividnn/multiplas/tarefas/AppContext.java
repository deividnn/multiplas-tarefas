/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deividnn.multiplas.tarefas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author deivid
 */
@WebListener
public class AppContext implements ServletContextListener {

    //agendador de tarefas
    public static Scheduler agendador;
    //log das tarefas iniciadas
    public static List<String> logtarefa = new ArrayList<>();
    public static SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    //metodo chamado na inicializacao da aplicacao
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            logtarefa = new ArrayList<>();
            //instancia o agendador
            agendador = StdSchedulerFactory.getDefaultScheduler();
            // inicia o agendador de tarefas
            agendador.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //grava log das tarefas 
    public static void gravalog(String msg) {

        if (logtarefa == null) {
            logtarefa = new ArrayList<>();
        }
        logtarefa.add(sd.format(
                Calendar.getInstance().getTime())
                + ":" + msg);
     

    }

    //metodo chamado no desligamento da aplicacao
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            //pausa todas as tarefas
            agendador.pauseAll();
            //limpa as instancia das tarefas
            agendador.clear();
            //deliga o agendador
            agendador.shutdown(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
