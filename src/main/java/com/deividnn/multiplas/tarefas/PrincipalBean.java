/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deividnn.multiplas.tarefas;

import static com.deividnn.multiplas.tarefas.AppContext.agendador;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 *
 * @author deivid
 */
@ManagedBean
@SessionScoped
public class PrincipalBean implements Serializable {

    //lista de tarefas adicionadas
    public List<Tarefa> tarefas;
    //tarefa usada no formulario de cadastro
    public Tarefa tarefa;
    //lista de log de acoes
    public List<String> log;
    //lista de log das tarefas
    public List<String> logtarefas;
    //lista de metodos 
    private Map<String, String> metodos;
    SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    //inicializa as variaveis no scopo session( os dados sao valido enquanto ha sessao valida)
    @PostConstruct
    public void init() {
        log = new ArrayList<>();
        logtarefas = new ArrayList<>();
        tarefa = new Tarefa();
        tarefas = new ArrayList<>();

        metodos = new HashMap<>();
        metodos.put("Somar", "metodoDeSomar");
        metodos.put("Media", "metodoDeMedia");
        metodos.put("Fatorial", "metodoDeFatorial");

    }

    //pega logs das tarefas em execucao
    public void pegalogs() {
        logtarefas = new ArrayList<>();
        for (String s : AppContext.logtarefa) {
            logtarefas.add(s);
        }
    }

    //grava log das acoes
    public void gravalog(String msg) {

        if (log == null) {
            log = new ArrayList<>();
        }
        log.add(sd.format(
                Calendar.getInstance().getTime())
                + ":" + msg);
        //reverte a ordem da lista de string
        Collections.sort(log, Collections.reverseOrder());
        //atualiza o id log na pagina jsf
        RequestContext.getCurrentInstance().update("form:log");
    }

    //cadastra uma nova tarefa
    public void cadastrarTarefa() {
        try {
            tarefa.setId(tarefas.size() + 1);
            tarefa.setStatus("Inativo");

            //verifica se ja existe a tarefa na lista
            boolean jaexiste = false;
            for (Tarefa ta : tarefas) {
                if (ta.getNome().equals(tarefa.getNome())
                        || ta.getRotina().equals(tarefa.getRotina())) {
                    jaexiste = true;
                    break;
                }
            }

            if (!jaexiste) {
                //adiciona a tarefa e gravalog
                tarefas.add(tarefa);
                gravalog(" cadastrou a tarefa " + tarefa.getNome());
                tarefa = new Tarefa();
            } else {
                FacesContext.getCurrentInstance().addMessage("tarefa ja existe", new FacesMessage("tarefa ja existe"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("erro", new FacesMessage(e.toString()));
        }

    }

    //inicia a tarefa no agendador
    public void iniciarTarefa(Tarefa t) {
        try {
            if (!t.getStatus().equals("Ativo")) {
                t.setStatus("Ativo");

                criarTarefaNoAgendador(t);
                gravalog(" iniciou a tarefa " + t.getNome());
            } else {
                FacesContext.getCurrentInstance().addMessage("aviso", new FacesMessage("tarefa ja ativa"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("erro", new FacesMessage(e.toString()));
        }
    }

    //inicia todas as tarefas
    public void iniciarTodasTarefa() {
        try {
            for (Tarefa t : tarefas) {

                if (!t.getStatus().equals("Ativo")) {
                    t.setStatus("Ativo");
                    criarTarefaNoAgendador(t);
                    gravalog(" iniciou a tarefa " + t.getNome());
                } else {
                    FacesContext.getCurrentInstance().addMessage("aviso", new FacesMessage("tarefa ja ativa"));
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("erro", new FacesMessage(e.toString()));
        }
    }

    //define a tarefa no agendador de acordo com metodo
    public void criarTarefaNoAgendador(Tarefa t) throws SchedulerException {
        //verifica se a tarefa esta pausada
        if (agendador.checkExists(new JobKey(t.getNome()))) {
            agendador.resumeJob(new JobKey(t.getNome()));
        } else {

            switch (t.getMetodo()) {
                case "metodoDeSomar": {
                    //detalhe da tarefa
                    JobDetail job = JobBuilder.newJob(TarefaSomar.class).withIdentity(t.getNome()).build();
                    //gatilho da tarefa de acordo com a expresao cron
                    Trigger tr = TriggerBuilder.newTrigger()
                            .withIdentity("tr" + t.getNome(), "gtr" + t.getNome())
                            .withSchedule(cronSchedule(t.getNome(),
                                    t.getRotina())).build();
                    agendador.scheduleJob(job, tr);
                    break;
                }
                case "metodoDeFatorial": {
                    //detalhe da tarefa
                    JobDetail job = JobBuilder.newJob(TarefaFatorial.class).withIdentity(t.getNome()).build();
                    //gatilho da tarefa de acordo com a expresao cron
                    Trigger tr = TriggerBuilder.newTrigger()
                            .withIdentity("tr" + t.getNome(), "gtr" + t.getNome())
                            .withSchedule(cronSchedule(t.getNome(),
                                    t.getRotina())).build();
                    agendador.scheduleJob(job, tr);
                    break;
                }
                case "metodoDeMedia": {
                    //detalhe da tarefa
                    JobDetail job = JobBuilder.newJob(TarefaMedia.class).withIdentity(t.getNome()).build();
                    //gatilho da tarefa de acordo com a expresao cron
                    Trigger tr = TriggerBuilder.newTrigger()
                            .withIdentity("tr" + t.getNome(), "gtr" + t.getNome())
                            .withSchedule(cronSchedule(t.getNome(),
                                    t.getRotina())).build();
                    agendador.scheduleJob(job, tr);
                    break;
                }
                default:
                    break;
            }
        }
    }

    public static CronScheduleBuilder cronSchedule(String desc, String cronExpression) {
        System.out.println(desc + "->(" + cronExpression + ")");
        return CronScheduleBuilder.cronSchedule(cronExpression);
    }

    //pausa uma tarefa
    public void pausaTarefa(Tarefa t) {
        try {
            if (!t.getStatus().equals("Pausada")) {
                t.setStatus("Pausada");
                agendador.pauseJob(new JobKey(t.getNome()));
                gravalog(" pausou a tarefa " + t.getNome());
            } else {
                FacesContext.getCurrentInstance().addMessage("aviso", new FacesMessage("tarefa ja pausada"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("erro", new FacesMessage(e.toString()));
        }

    }

    //pausa todas as taredas
    public void pausaTodasTarefa() {
        try {
            for (Tarefa t : tarefas) {

                if (!t.getStatus().equals("Pausada")) {
                    t.setStatus("Pausada");
                    agendador.pauseJob(new JobKey(t.getNome()));
                    gravalog(" pausou a tarefa " + t.getNome());
                } else {
                    FacesContext.getCurrentInstance().addMessage("aviso", new FacesMessage("tarefa ja pausada"));
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("erro", new FacesMessage(e.toString()));

        }

    }

    //exclui a tarefa da lista e do agendador
    public void excluirTarefa(Tarefa t) {
        try {
            for (Iterator<Tarefa> it = tarefas.iterator(); it.hasNext();) {
                Tarefa next = it.next();
                if (next.getId() == t.getId()) {
                    it.remove();
                    agendador.deleteJob(new JobKey(t.getNome()));
                    gravalog(" excluiu a tarefa " + t.getNome());
                    break;
                }

            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("erro", new FacesMessage(e.toString()));
        }

    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public SimpleDateFormat getSd() {
        return sd;
    }

    public void setSd(SimpleDateFormat sd) {
        this.sd = sd;
    }

    public Map<String, String> getMetodos() {
        return metodos;
    }

    public void setMetodos(Map<String, String> metodos) {
        this.metodos = metodos;
    }

    public List<String> getLogtarefas() {
        return logtarefas;
    }

    public void setLogtarefas(List<String> logtarefas) {
        this.logtarefas = logtarefas;
    }

}
