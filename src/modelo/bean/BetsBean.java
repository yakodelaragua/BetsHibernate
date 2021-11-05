package modelo.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.event.SelectEvent;

import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import domain.Event;
import domain.Question;
import exceptions.EventFinished;
import exceptions.QuestionAlreadyExist;

public class BetsBean {

	private Date fecha;
	private String question;
	private float minBet;
	private ArrayList<Question> preguntas = new ArrayList<Question>();
	private static Event event;
	private static List<Event> eventos = new ArrayList<Event>();
	BLFacade facade;
	private UIComponent component;

	public BetsBean() {
		facade = new BLFacadeImplementation();

	}

	public UIComponent getComponent() {
		return component;
	}

	public void setComponent(UIComponent component) {
		this.component = component;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<Question> getQuestions(){
		System.out.println("Carga las preguntas " + preguntas.size());
		return preguntas;
	}

	public void setQuestions(Vector<Question> questions) {
		for(Question q : questions) {
			preguntas.add(q);
		}
	}
	public float getMinBet() {
		return minBet;
	}

	public void setMinBet(float minBet) {
		this.minBet = minBet;
	}


	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Event getEvent() {
		return event;
	}

	@SuppressWarnings("static-access")
	public void setEvent(Event event) {
		System.out.println(event);
		this.event = event;
	}

	public List<Event> getEventos() {
		return eventos;
	}

	@SuppressWarnings("static-access")
	public void setEventos(List<Event> events) {
		this.eventos = events;
	}

	public void onDateSelect(SelectEvent event) {
		eventos = facade.getEvents((Date) event.getObject());
		SimpleDateFormat dt1 = new SimpleDateFormat("MMMMMMMMMMMMM dd, yyyy ");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Events: " +  dt1.format(fecha)));
		if(eventos.size() != 0) {
			BetsBean.event = eventos.get(0);
			Vector<Question> resul = new Vector<Question>();
			List<Question> qu = BetsBean.event.getQuestions();
			for(Question q: qu) {
				resul.add(q);
			}
			setQuestions(resul);
		} else {
			BetsBean.event = null;
			preguntas= new ArrayList<Question>();
		}
	}

	public void onEventSelect(AjaxBehaviorEvent event) {
		System.out.println("Inicializar preguntas");
		preguntas = new ArrayList<Question>();
		List<Question> questions = new ArrayList<Question>();
		questions = BetsBean.event.getQuestions();
		for (Question q : questions){
			System.out.println(q);
			preguntas.add(q);
		}
	}

	public static Event getObject(String newValue) {
		for (Event e : eventos) {
			if (e.toString().equals(newValue)) {
				event = e;
				return e;
			}
		}
		return null;
	}


	public String close() {
		System.out.println("Reiniciando variables");

		fecha = new Date();

		event = null;
		eventos = new ArrayList<Event>();

		question = null;
		preguntas = new ArrayList<Question>();

		minBet = 0;

		return "close";

	}

	public void createQuestion() {
		if(event == null) {
			FacesContext.getCurrentInstance().addMessage(component.getClientId(), new FacesMessage("Error: Select an event"));
		}else {
			if (question.equals("")) {
				FacesContext.getCurrentInstance().addMessage(component.getClientId(), new FacesMessage("Error: Introduce a question"));

			} else if (minBet < 0) {
				FacesContext.getCurrentInstance().addMessage(component.getClientId(), new FacesMessage("Error: Introduce a positive number"));
			} else {
				try {
					facade.createQuestion(event, question, minBet);
					FacesContext.getCurrentInstance().addMessage(component.getClientId(), new FacesMessage("Question created"));
				} catch (EventFinished e) {
					FacesContext.getCurrentInstance().addMessage(component.getClientId(), new FacesMessage("Error: Event has finished"));

				} catch (QuestionAlreadyExist e) {
					FacesContext.getCurrentInstance().addMessage(component.getClientId(), new FacesMessage("Error: Question already exists"));
				}
			}
		}
	}
}

