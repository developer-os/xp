module app_wizard_form {

    export class FieldSetView extends LayoutView {

        private fieldSet:api_schema_content_form.FieldSet;

        private formItemViews:FormItemView[] = [];

        constructor(fieldSet:api_schema_content_form.FieldSet) {
            super(fieldSet, "FieldSetView", "field-set-view");

            this.fieldSet = fieldSet;
            this.doLayout();
        }

        getData():api_data.Data[] {
            return null;
        }

        private doLayout() {

            var label = new FieldSetLabel(this.fieldSet);
            this.appendChild(label);

            var wrappingDiv = new api_dom.DivEl(null, "field-set-container");
            this.appendChild(wrappingDiv);

            this.fieldSet.getFormItems().forEach((formItem:api_schema_content_form.FormItem) => {
                if (formItem instanceof api_schema_content_form.FormItemSet) {
                    var formItemSet:api_schema_content_form.FormItemSet = <api_schema_content_form.FormItemSet>formItem;
                    console.log("FieldSetView.doLayout() laying out FormItemSet: ", formItemSet);
                    var formItemSetView = new FormItemSetView(formItemSet);
                    wrappingDiv.appendChild(formItemSetView);
                    this.formItemViews.push(formItemSetView);
                }
                else if (formItem instanceof api_schema_content_form.Input) {
                    var input:api_schema_content_form.Input = <api_schema_content_form.Input>formItem;
                    console.log("FieldSetView.doLayout()  laying out Input: ", input);
                    var inputContainerView = new app_wizard_form.InputContainerView(input);
                    wrappingDiv.appendChild(inputContainerView);
                    this.formItemViews.push(inputContainerView);
                }
            });

        }
    }
}