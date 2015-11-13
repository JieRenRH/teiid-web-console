/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.teiid;

import static com.google.gwt.dom.client.Style.Unit.PX;

import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.FormValidator;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.dmr.client.ModelNode;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class TeiidModelForm<T> {

    private Form<T> form;
    private Class<T> type;
    private FormItem<?>[] fields;
    private Label formValidationError;
    private Persistable<T> presenter;
    private DefaultCellTable<T> table;

    public TeiidModelForm(Class<T> type, Persistable<T> presenter, FormItem<?>... fields) {
        this.type = type;
        this.presenter = presenter;
        this.fields = fields;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout");

        this.form = new Form<T>(type);
        this.form.setNumColumns(2);
        
        this.form.addFormValidator(new FormValidator() {
            @Override
            public void validate(List<FormItem> formItems, FormValidation outcome) {
                validateForm(outcome);
            }
        });
        
        FormToolStrip<T> attributesToolStrip = new FormToolStrip<T>(this.form,
                new FormToolStrip.FormCallback<T>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.save(form.getEditedEntity(),form.getChangedValues());
                    }

                    @Override
                    public void onDelete(T entity) {
                        // this is not delete, it is Cancel
                    }
                });
        layout.add(attributesToolStrip.asWidget());
        
        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "teiid");
                return address;
            }
        }, this.form);
        
        layout.add(helpPanel.asWidget());

        this.formValidationError = new Label("Form is invalid!");
        this.formValidationError.addStyleName("form-error-desc");
        this.formValidationError.getElement().getStyle().setLineHeight(9, PX);
        this.formValidationError.getElement().getStyle().setMarginBottom(5, PX);
        this.formValidationError.setVisible(false);
        layout.add(formValidationError.asWidget());

        if (this.table != null) {
            this.form.bind(this.table);
        }
        
        this.form.setFields(this.fields);
        this.form.setEnabled(false);

        layout.add(this.form.asWidget());

        return layout;
    }

    public void edit(T t) {
        form.edit(t);
    }

    public void clearValues() {
        form.clearValues();
    }

    protected FormValidation validateForm(FormValidation formValidation) {
        return formValidation;
    }
    
    public void setTable(DefaultCellTable<T> table) {
        this.table = table;
    }
}
