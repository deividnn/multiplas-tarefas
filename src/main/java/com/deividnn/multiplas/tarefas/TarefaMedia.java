/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deividnn.multiplas.tarefas;

import java.io.Serializable;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author deivid
 */
public class TarefaMedia implements Job, Serializable {

    public TarefaMedia() {

    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            metodoDeMedia();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void metodoDeMedia() {
        System.out.println("media");
        AppContext.gravalog("fazendo media");
    }

}
