/***
 * Copyright (c) 2009-2020 Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("Non-Profit OSL" 3.0)
 * License text at https://github.com/jflamy/owlcms4/blob/master/LICENSE.txt
 */

package app.owlcms.ui.lifting;

import java.util.Collection;

import org.slf4j.LoggerFactory;

import com.flowingcode.vaadin.addons.ironicons.AvIcons;
import com.google.common.collect.ImmutableList;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;

import app.owlcms.components.elements.AthleteTimerElement;
import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.group.Group;
import app.owlcms.fieldofplay.BreakType;
import app.owlcms.fieldofplay.FOPEvent;
import app.owlcms.fieldofplay.FOPState;
import app.owlcms.init.OwlcmsSession;
import app.owlcms.ui.shared.AthleteGridContent;
import app.owlcms.ui.shared.AthleteGridLayout;
import app.owlcms.ui.shared.BreakDialog;
import app.owlcms.ui.shared.BreakManagement.CountdownType;
import app.owlcms.utils.LoggerUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Class AnnouncerContent.
 */
@SuppressWarnings("serial")
@Route(value = "lifting/timekeeper", layout = AthleteGridLayout.class)
public class TimekeeperContent extends AthleteGridContent implements HasDynamicTitle, PageConfigurator {

    final private static Logger logger = (Logger) LoggerFactory.getLogger(TimekeeperContent.class);
    final private static Logger uiEventLogger = (Logger) LoggerFactory.getLogger("UI" + logger.getName());
    static {
        logger.setLevel(Level.INFO);
        uiEventLogger.setLevel(Level.INFO);
    }

    private ShortcutRegistration startReg;
    private ShortcutRegistration stopReg;

    public TimekeeperContent() {
        super();
        setTopBarTitle(getTranslation("Timekeeper"));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.vaadin.crudui.crud.CrudListener#add(java.lang.Object)
     */
    @Override
    public Athlete add(Athlete athlete) {
        // do nothing
        return athlete;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.vaadin.crudui.crud.CrudListener#delete(java.lang.Object)
     */
    @Override
    public void delete(Athlete Athlete) {
        // do nothing;
    }

    @Override
    public Collection<Athlete> findAll() {
        return ImmutableList.of();
    }

    /**
     * @see com.vaadin.flow.router.HasDynamicTitle#getPageTitle()
     */
    @Override
    public String getPageTitle() {
        return getTranslation("Timekeeper");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.vaadin.crudui.crud.CrudListener#update(java.lang.Object)
     */
    @Override
    public Athlete update(Athlete athlete) {
        // do nothing
        return athlete;
    }

    @Override
    protected HorizontalLayout announcerButtons(FlexLayout announcerBar) {
        createStartTimeButton();
        createStopTimeButton();
        create1minButton();
        create2MinButton();

        HorizontalLayout buttons = new HorizontalLayout(startTimeButton, stopTimeButton, _1min, _2min);
        buttons.setAlignItems(FlexComponent.Alignment.BASELINE);
        return buttons;
    }

    /**
     * @see app.owlcms.ui.shared.AthleteGridContent#breakButtons(com.vaadin.flow.component.orderedlayout.FlexLayout)
     */
    @Override
    protected HorizontalLayout breakButtons(FlexLayout announcerBar) {
        breakDialog = new BreakDialog(this);
        breakButton = new Button(AvIcons.AV_TIMER.create(), (e) -> {
            breakDialog.open();
        });
        return layoutBreakButtons();
    }

    /**
     * @see app.owlcms.ui.shared.AthleteGridContent#createInitialBar()
     */
    @Override
    protected void createInitialBar() {
        logger.debug("AnnouncerContent creating top bar");
        topBar = getAppLayout().getAppBarElementWrapper();
        topBar.removeAll();
        initialBar = true;

        createTopBarGroupSelect();
        createTopBarLeft();

        introCountdownButton = new Button(getTranslation("introCountdown"), AvIcons.AV_TIMER.create(), (e) -> {
            BreakDialog dialog = new BreakDialog(this, BreakType.BEFORE_INTRODUCTION, CountdownType.TARGET);
            dialog.open();
        });
        introCountdownButton.getElement().setAttribute("theme", "primary contrast");

        warning = new H3();
        warning.getStyle().set("margin-top", "0").set("margin-bottom", "0");
        HorizontalLayout topBarRight = new HorizontalLayout();
        topBarRight.add(warning, introCountdownButton);
        topBarRight.setSpacing(true);
        topBarRight.setPadding(true);
        topBarRight.setAlignItems(FlexComponent.Alignment.CENTER);

        topBar.removeAll();
        topBar.setSizeFull();
        topBar.add(getTopBarLeft(), topBarRight);

        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);
        topBar.setFlexGrow(0.0, getTopBarLeft());
        topBar.setFlexGrow(1.0, topBarRight);
        
        if (timeField != null) {
            timeField.detach();
        }
        this.removeAll();
    }

    /**
     * @see app.owlcms.ui.shared.AthleteGridContent#createTopBar()
     */
    @Override
    protected void createTopBar() {
        topBar = getAppLayout().getAppBarElementWrapper();
        topBar.removeAll();
        initialBar = false;

        createTopBarGroupSelect();
        createTopBarLeft();

        lastName = new H1();
        lastName.setText("\u2013");
        lastName.getStyle().set("margin", "0px 0px 0px 0px");

        setFirstNameWrapper(new H2(""));
        getFirstNameWrapper().getStyle().set("margin", "0px 0px 0px 0px");
        firstName = new Span("");
        firstName.getStyle().set("margin", "0px 0px 0px 0px");
        startNumber = new Span("");
        Style style = startNumber.getStyle();
        style.set("margin", "0px 0px 0px 1em");
        style.set("padding", "0px 0px 0px 0px");
        style.set("border", "2px solid var(--lumo-primary-color)");
        style.set("font-size", "90%");
        style.set("width", "1.4em");
        style.set("text-align", "center");
        style.set("display", "inline-block");
        getFirstNameWrapper().add(firstName, startNumber);
        Div fullName = new Div(lastName, getFirstNameWrapper());

        attempt = new H2();
        weight = new H2();
        weight.setText("");

        clearVerticalMargins(attempt);
        clearVerticalMargins(weight);

        buttons = null;
        decisions = null;
        breaks = breakButtons(topBar);

        topBar.setSizeFull();
        topBar.add(getTopBarLeft(), fullName, attempt, weight, breaks);
        if (breaks != null) {
            topBar.add(breaks);
        }

        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);
        topBar.setAlignSelf(Alignment.CENTER, attempt, weight);
        topBar.setFlexGrow(0.5, fullName);
        topBar.setFlexGrow(0.0, getTopBarLeft());
        // this hides the back arrow
        getAppLayout().setMenuVisible(false);
        
        createBottom();
    }

    @Override
    protected HorizontalLayout decisionButtons(FlexLayout announcerBar) {
        HorizontalLayout decisions = new HorizontalLayout();
        return decisions;
    }

    @Override
    protected void init() {
        crudLayout = null;
    }

    private void createBottom() {
        this.removeAll();
        if (timeField == null) {
            timeField = new AthleteTimerElement(this);
        }
        VerticalLayout time = new VerticalLayout();
        time.setWidth("50%");

        time.getElement().getStyle().set("font-size", "4em");
        time.getElement().getStyle().set("font-weight", "bold");
        time.setAlignItems(Alignment.CENTER);
        time.setAlignSelf(Alignment.CENTER, timeField);
        centerH(timeField,time);
        this.add(time);
        
        startTimeButton = new Button(AvIcons.PLAY_ARROW.create());
        startTimeButton.addClickListener(e -> {
            OwlcmsSession.withFop(fop -> {
                fop.getFopEventBus().post(new FOPEvent.TimeStarted(this.getOrigin()));
                if (startTimeButton != null) {
                    startTimeButton.getElement().setAttribute("theme", "secondary");
                }
                if (stopTimeButton != null) {
                    stopTimeButton.getElement().setAttribute("theme", "primary error");
                }
            });
        });
        startTimeButton.getElement().setAttribute("theme", "primary success");
        
        stopTimeButton = new Button(AvIcons.PAUSE.create());
        stopTimeButton.addClickListener(e1 -> {
            OwlcmsSession.withFop(fop -> {
                fop.getFopEventBus().post(new FOPEvent.TimeStopped(this.getOrigin()));
                if (startTimeButton != null) {
                    startTimeButton.getElement().setAttribute("theme", "primary success");
                }
                if (stopTimeButton != null) {
                    stopTimeButton.getElement().setAttribute("theme", "secondary");
                }
            });
        });
        stopTimeButton.getElement().setAttribute("theme", "secondary");
        
        registerShortcuts();
        
        _1min = new Button("1:00", (e2) -> {
            OwlcmsSession.withFop(fop -> {
                fop.getFopEventBus().post(new FOPEvent.ForceTime(60000, this.getOrigin()));
            });
        });
        _1min.getElement().setAttribute("theme", "icon");
        
        _2min = new Button("2:00", (e3) -> {
            OwlcmsSession.withFop(fop -> {
                fop.getFopEventBus().post(new FOPEvent.ForceTime(120000, this.getOrigin()));
            });
        });
        _2min.getElement().setAttribute("theme", "icon");
        
        startTimeButton.setSizeFull();
        stopTimeButton.setSizeFull();
        _1min.setSizeFull();
        _2min.setSizeFull();
        
        VerticalLayout resets = new VerticalLayout(_1min, _2min);
        resets.setSizeFull();
        resets.setMargin(false);
        resets.setPadding(false);
        
        buttons = new HorizontalLayout(startTimeButton, stopTimeButton, resets);
        time.getStyle().set("margin-top", "0.75em");
        time.getStyle().set("margin-bottom", "0.75em");
        buttons.setWidth("75%");
        buttons.setHeight("40%");
        buttons.setAlignItems(FlexComponent.Alignment.CENTER);
        buttons.getStyle().set("--lumo-font-size-m", "2rem");
        
        centerHW(buttons, this);
        hideButtons();
    }

    @Override
    protected void syncWithFOP(boolean refreshGrid) {
        OwlcmsSession.withFop((fop) -> {
            Group fopGroup = fop.getGroup();
            logger.debug("syncing FOP, group = {}, {}", fopGroup, LoggerUtils.whereFrom(2));

            Athlete curAthlete2 = fop.getCurAthlete();
            FOPState state = fop.getState();
            if (state == FOPState.INACTIVE || (state == FOPState.BREAK && fop.getGroup() == null)) {
                logger.debug("initial: {} {} {} {}", state, fop.getGroup(), curAthlete2,
                        curAthlete2 == null ? 0 : curAthlete2.getAttemptsDone());
                createInitialBar();
                warning.setText(getTranslation("IdlePlatform"));
                if (curAthlete2 == null || curAthlete2.getAttemptsDone() >= 6 || fop.getLiftingOrder().size() == 0) {
                    topBarWarning(fop.getGroup(), curAthlete2 == null ? 0 : curAthlete2.getAttemptsDone(),
                            fop.getState(), fop.getLiftingOrder());
                }
            } else {
                logger.debug("active: {}", state);
                createTopBar();
                if (state == FOPState.BREAK) {
                    if (buttons != null) {
                        hideButtons();
                    }
                    if (decisions != null) {
                        decisions.setVisible(false);
                    }
                    busyBreakButton();
                } else {
                    if (buttons != null) {
                        showButtons();
                    }
                    if (decisions != null) {
                        decisions.setVisible(true);
                    }
                    if (breakButton == null) {
                        logger.debug("breakButton is null\n{}", LoggerUtils.stackTrace());
                        return;
                    }
                    breakButton.setText("");
                    quietBreakButton(false);
                }
                breakButton.setEnabled(true);

                Athlete curAthlete = curAthlete2;
                int timeRemaining = fop.getAthleteTimer().getTimeRemaining();
                super.doUpdateTopBar(curAthlete, timeRemaining);
            }
        });
    }

    private void showButtons() {
        buttons.setVisible(true);
        timeField.getElement().setVisible(true);
        registerShortcuts();
    }

    private void registerShortcuts() {
        startReg = startTimeButton.addClickShortcut(Key.COMMA);
        stopReg = stopTimeButton.addClickShortcut(Key.PERIOD);
    }

    private void hideButtons() {
        buttons.setVisible(false);
        timeField.getElement().setVisible(false);
        unregisterShortcuts();
    }

    private void unregisterShortcuts() {
        if (startReg != null) {startReg.remove();}
        if (stopReg != null) {stopReg.remove();}
    }

    @Override
    protected void doUpdateTopBar(Athlete athlete, Integer timeAllowed) {
        showButtons();
        super.doUpdateTopBar(athlete, timeAllowed);
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        settings.addMetaTag("mobile-web-app-capable", "yes");
        settings.addMetaTag("apple-mobile-web-app-capable", "yes");
        settings.addLink("shortcut icon", "frontend/images/owlcms.ico");
        settings.addFavIcon("icon", "frontend/images/logo.png", "96x96");
        settings.setViewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes");
    }
    

}
