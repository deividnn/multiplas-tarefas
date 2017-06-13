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
public class TarefaFatorial implements Job, Serializable {

    public TarefaFatorial() {

    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            metodoDeFatorial();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void metodoDeFatorial() {
        System.out.println("fatorial");
        AppContext.gravalog("calculando o fatorial");
    }

}
