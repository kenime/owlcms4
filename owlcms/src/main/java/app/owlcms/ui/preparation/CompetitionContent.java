/***
 * Copyright (c) 2018-2019 Jean-François Lamy
 * 
 * This software is licensed under the the Apache 2.0 License amended with the
 * Commons Clause.
 * License text at https://github.com/jflamy/owlcms4/master/License
 * See https://redislabs.com/wp-content/uploads/2018/10/Commons-Clause-White-Paper.pdf
 */
package app.owlcms.ui.preparation;

import java.util.Arrays;
import java.util.Locale;

import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.form.impl.field.provider.ComboBoxProvider;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.CrudLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;

import app.owlcms.data.competition.Competition;
import app.owlcms.data.competition.CompetitionRepository;
import app.owlcms.ui.crudui.OwlcmsCrudFormFactory;
import app.owlcms.ui.home.ContentWrapping;

/**
 * The Class PreparationNavigationContent.
 */
@SuppressWarnings("serial")
@Route(value = "preparation/competition", layout = CompetitionLayout.class)
public class CompetitionContent extends VerticalLayout
		implements ContentWrapping, CrudLayout {

	/**
	 * Instantiates a new preparation navigation content.
	 */
	public CompetitionContent() {
		DefaultCrudFormFactory<Competition> factory = createFormFactory();
		Component form = factory.buildNewForm(CrudOperation.UPDATE, Competition.getCurrent(), false, null, event -> {
            try {
                this.update(Competition.getCurrent());
            } catch (IllegalArgumentException ignore) {
            } catch (Exception e2) {
                throw e2;
            }
        });	
		fillH(form, this);	
	}
	
	/**
	 * Define the form used to edit a given athlete.
	 * 
	 * @return the form factory that will create the actual form on demand
	 */
	protected DefaultCrudFormFactory<Competition>  createFormFactory() {
		DefaultCrudFormFactory<Competition>  athleteEditingFormFactory = createCompetitionEditingFormFactory();
		createFormLayout(athleteEditingFormFactory);
		return athleteEditingFormFactory;
	}

	/**
	 * The content and ordering of the editing form
	 * 
	 * @param crudFormFactory the factory that will create the form using this information
	 */
	private void createFormLayout(DefaultCrudFormFactory<Competition> crudFormFactory) {
		crudFormFactory.setVisibleProperties(
			"competitionName",
			"competitionDate",
			"competitionOrganizer",
			"competitionSite",
			"competitionCity",
			"federation",
			"federationAddress",
			"federationEMail",
			"federationWebSite",
			"defaultLocale",
			"protocolFileName",
			"resultTemplateFileName",
			"enforce20kgRule",
			"masters",
			"useBirthYear"		
			);
		crudFormFactory.setFieldProvider("defaultLocale",
            new ComboBoxProvider<Locale>("Locale", Arrays.asList(Locale.ENGLISH,Locale.FRENCH), new TextRenderer<>(Locale::getDisplayName), Locale::getDisplayName));
		crudFormFactory.setFieldType("competitionDate", LocalDateField.class);
	}
	
	/**
	 * Create the actual form generator with all the conversions and validations required
	 * 
	 * @return the actual factory with field binding and validations
	 */
	private DefaultCrudFormFactory<Competition> createCompetitionEditingFormFactory() {
		return new OwlcmsCrudFormFactory<Competition>(Competition.class) {
			@SuppressWarnings({ "rawtypes" })
			@Override
			protected void bindField(HasValue field, String property, Class<?> propertyType) {
				//Binder.BindingBuilder bindingBuilder = binder.forField(field);
				super.bindField(field, property, propertyType);
			}
		};
	}

	public Competition update(Competition domainObjectToUpdate) {
		return CompetitionRepository.save(domainObjectToUpdate);
	}

	@Override
	public void setMainComponent(Component component) {
		this.removeAll();
		this.add(component);
	}

	@Override
	public void addFilterComponent(Component component) {
	}

	@Override
	public void addToolbarComponent(Component component) {
	}

	@Override
	public void showForm(CrudOperation operation, Component form, String caption) {
		this.removeAll();
		this.add(form);
	}

	@Override
	public void hideForm() {
	}

	@Override
	public void showDialog(String caption, Component form) {
	}

}
