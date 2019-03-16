/***
 * Copyright (c) 2018-2019 Jean-François Lamy
 * 
 * This software is licensed under the the Apache 2.0 License amended with the
 * Commons Clause.
 * License text at https://github.com/jflamy/owlcms4/master/License
 * See https://redislabs.com/wp-content/uploads/2018/10/Commons-Clause-White-Paper.pdf
 */

package app.owlcms.ui.lifting;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.vaadin.crudui.crud.CrudListener;
import org.vaadin.crudui.crud.impl.GridCrud;

import com.github.appreciated.app.layout.router.AppLayoutRouterLayout;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.athlete.AthleteRepository;
import app.owlcms.data.athleteSort.AthleteSorter;
import app.owlcms.data.athleteSort.AthleteSorter.Ranking;
import app.owlcms.data.group.Group;
import app.owlcms.data.group.GroupRepository;
import app.owlcms.init.OwlcmsFactory;
import app.owlcms.state.FOPEvent;
import app.owlcms.state.FieldOfPlayState;
import app.owlcms.state.UIEvent;
import app.owlcms.ui.appLayout.AppLayoutContent;
import app.owlcms.ui.crudui.OwlcmsCrudFormFactory;
import app.owlcms.ui.crudui.OwlcmsCrudLayout;
import app.owlcms.ui.crudui.OwlcmsGridCrud;
import app.owlcms.ui.home.ContentWrapping;
import app.owlcms.ui.home.QueryParameterReader;
import app.owlcms.ui.home.SafeEventBusRegistration;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Class AnnouncerContent.
 */
@SuppressWarnings("serial")
@Route(value = "group/results", layout = ResultsLayout.class)
public class ResultsContent extends VerticalLayout
		implements CrudListener<Athlete>, QueryParameterReader, ContentWrapping, SafeEventBusRegistration, UIEventProcessor, AppLayoutContent {

	// @SuppressWarnings("unused")
	final private Logger logger = (Logger) LoggerFactory.getLogger(ResultsContent.class);
	final private Logger uiEventLogger = (Logger) LoggerFactory.getLogger("owlcms.uiEventLogger");

	protected void initLoggers() {
		logger.setLevel(Level.DEBUG);
		uiEventLogger.setLevel(Level.DEBUG);
	}

	private Location location;
	private UI locationUI;
	private GridCrud<Athlete> crud;
	private EventBus uiEventBus;
	private AppLayoutRouterLayout parentLayout;
	
	private ComboBox<Group> groupFilter = new ComboBox<>();
	private Group currentGroup;
	private FieldOfPlayState currentFop;

	/**
	 * Instantiates a new announcer content.
	 * Does nothing. Content is created in {@link #setParameter(BeforeEvent, String)} after URL parameters are parsed.
	 */
	public ResultsContent() {
		initLoggers();
	}


	/* (non-Javadoc)
	 * @see com.vaadin.flow.component.Component#onAttach(com.vaadin.flow.component.AttachEvent)
	 */
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		crud = getGridCrud();
		fillHW(crud, this);
		((ResultsLayout)getParentLayout()).setLayoutGroup(currentGroup); // from http query parameters
	}

	/**
	 * Parse the http query parameters
	 * 
	 * Note: because we have the @Route, the parameters are parsed *before* our parent layout is created.
	 * 
	 * @param event Vaadin navigation event
	 * @param parameter null in this case -- we don't want a vaadin "/" parameter. This allows us to add query parameters instead.
	 * 
	 * @see app.owlcms.ui.home.QueryParameterReader#setParameter(com.vaadin.flow.router.BeforeEvent, java.lang.String)
	 */
	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		location = event.getLocation();
		locationUI = event.getUI();
				
		QueryParameters queryParameters = location.getQueryParameters();
		Map<String, List<String>> parametersMap = queryParameters.getParameters(); // immutable
		HashMap<String, List<String>> params = new HashMap<String, List<String>>(parametersMap);
		
		logger.debug("parsing query parameters");
		List<String> groupNames = params.get("group");
		if (groupNames != null && !groupNames.isEmpty()) {
			String groupName = groupNames.get(0);
			currentGroup = GroupRepository.findByName(groupName);
		} else {
			currentGroup = null;
		}
		if (currentGroup != null) params.put("group",Arrays.asList(currentGroup.getName()));
		params.remove("fop");
		
		// change the URL to reflect group
		event.getUI().getPage().getHistory().replaceState(null, new Location(location.getPath(),new QueryParameters(params)));
	}
	/* (non-Javadoc)
	 * @see app.owlcms.ui.lifting.UIEventProcessor#updateGrid(app.owlcms.state.UIEvent.LiftingOrderUpdated)
	 */
	@Subscribe
	public void updateGrid(UIEvent.LiftingOrderUpdated e) {
		UIEventProcessor.uiAccess(crud, uiEventBus, e, () -> {
			crud.refreshGrid();
		});
	}
	
	/**
	 * Gets the grid crud.
	 *
	 * @return the grid crud
	 */
	public GridCrud<Athlete> getGridCrud() {
		OwlcmsCrudFormFactory<Athlete> crudFormFactory = new AthleteCardFormFactory(Athlete.class);

		Grid<Athlete> grid = new Grid<Athlete>(Athlete.class, false);
		ThemeList themes = grid.getThemeNames();
		themes.add("compact");
		themes.add("row-stripes");
		grid.setColumns("lastName", "firstName", "team", "category", "total");
		grid.getColumnByKey("lastName")
			.setHeader("Last Name");
		grid.getColumnByKey("firstName")
			.setHeader("First Name");
		grid.getColumnByKey("team")
			.setHeader("Team");
		grid.getColumnByKey("category")
			.setHeader("Category");
		grid.getColumnByKey("total")
			.setHeader("Total");

		OwlcmsCrudLayout owlcmsCrudLayout = new OwlcmsCrudLayout(Athlete.class);
		GridCrud<Athlete> crud = new OwlcmsGridCrud<Athlete>(Athlete.class,
				owlcmsCrudLayout,
				crudFormFactory,
				grid) {
			@Override
			protected void initToolbar() {}
			@Override
			protected void updateButtons() {}
		};
		
		logger.debug("creating filters: group={}",currentGroup);
		groupFilter.setPlaceholder("Group");
		groupFilter.setItems(GroupRepository.findAll());
		groupFilter.setItemLabelGenerator(Group::getName);
		// hide because the top bar has it
		groupFilter.getStyle().set("display", "none");
		groupFilter.addValueChangeListener(e -> {
				logger.debug("updating filters: group={}",e.getValue());
				currentGroup = e.getValue();
				updateURLLocation(locationUI, location, currentGroup);
				subscribeIfLifting(e.getValue());
		});
		
		crud.setCrudListener(this);
		crud.setClickRowToUpdate(true);
		crud.getCrudLayout()
			.addToolbarComponent(groupFilter);

		return crud;
	}

	private void subscribeIfLifting(Group nGroup) {
		logger.debug("subscribeIfLifting {}",nGroup);
		Collection<FieldOfPlayState> fops = OwlcmsFactory.getFOPs();
		currentFop = null;
		for (FieldOfPlayState fop: fops) {
			if (fop.getGroup() != null && fop.getGroup().equals(nGroup)) {
				logger.debug("subscribing to {} {}", fop, nGroup);
				try {fop.getUiEventBus().register(this);} catch (Exception ex) {}
				try {fop.getEventBus().register(this);} catch (Exception ex) {}
				currentFop = fop;
			} else {
				try {fop.getUiEventBus().unregister(this);} catch (Exception ex) {}
				try {fop.getEventBus().unregister(this);} catch (Exception ex) {}
			}
		}
		currentGroup = nGroup;
		refresh();
	}

	public void updateURLLocation(UI ui, Location location, Group newGroup) {
		// change the URL to reflect fop group
		HashMap<String, List<String>> params = new HashMap<String, List<String>>(location.getQueryParameters().getParameters());
		if (newGroup != null) {
			params.put("group",Arrays.asList(newGroup.getName()));
		} else {
			params.remove("group");
		}
		ui.getPage().getHistory().replaceState(null, new Location(location.getPath(),new QueryParameters(params)));
	}

	/* (non-Javadoc)
	 * @see org.vaadin.crudui.crud.CrudListener#add(java.lang.Object)
	 */
	@Override
	public Athlete add(Athlete Athlete) {
		checkFOP();
		AthleteRepository.save(Athlete);
		return Athlete;
	}

	/* (non-Javadoc)
	 * @see org.vaadin.crudui.crud.CrudListener#update(java.lang.Object)
	 */
	@Override
	public Athlete update(Athlete Athlete) {
		FieldOfPlayState fop = checkFOP();
		Athlete savedAthlete = AthleteRepository.save(Athlete);
		fop.getEventBus()
			.post(new FOPEvent.WeightChange(crud.getUI().get(), savedAthlete));
		return savedAthlete;
	}

	protected FieldOfPlayState checkFOP() {
		if (currentFop != null) {
			Notification.show("This group is currently lifting on FOP "+currentFop+". You cannot edit the results.");
		}
		return currentFop;
	}

	/* (non-Javadoc)
	 * @see org.vaadin.crudui.crud.CrudListener#delete(java.lang.Object)
	 */
	@Override
	public void delete(Athlete Athlete) {
		checkFOP();
		AthleteRepository.delete(Athlete);
	}

	/**
	 * Get the content of the grid.
	 * Invoked by refreshGrid.
	 * @see org.vaadin.crudui.crud.CrudListener#findAll()
	 */
	@Override
	public Collection<Athlete> findAll() {
		return AthleteSorter.resultsOrderCopy(AthleteRepository.findAllByGroupAndWeighIn(groupFilter.getValue(), true), Ranking.TOTAL);
	}

	/**
	 * @return the groupFilter
	 */
	public ComboBox<Group> getGroupFilter() {
		return groupFilter;
	}

	@Override
	public AppLayoutRouterLayout getParentLayout() {
		return parentLayout;
	}

	@Override
	public void setParentLayout(AppLayoutRouterLayout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public void refresh() {
		crud.refreshGrid();
	}
}
