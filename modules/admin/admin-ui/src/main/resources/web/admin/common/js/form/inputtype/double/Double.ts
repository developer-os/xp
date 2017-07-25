module api.content.form.inputtype.double {

    import BaseInputTypeNotManagingAdd = api.form.inputtype.support.BaseInputTypeNotManagingAdd;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import TextInput = api.ui.text.TextInput;
    import NumberHelper = api.util.NumberHelper;
    import InputOccurrenceView = api.form.inputtype.support.InputOccurrenceView;
    import ValueTypeDouble = api.data.ValueTypeDouble;

    export class Double extends BaseInputTypeNotManagingAdd<number> {

        private min: number = null;

        private max: number = null;

        private value: Value = null;

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
            this.readConfig(config);
        }

        getValueType(): ValueType {
            return ValueTypes.DOUBLE;
        }

        newInitialValue(): Value {
            return super.newInitialValue() || ValueTypes.DOUBLE.newNullValue();
        }

        private getConfigProperty(config: api.form.inputtype.InputTypeViewContext, propertyName: string ) {
            const configProperty = config.inputConfig[propertyName] ? config.inputConfig[propertyName][0] : {};

            return NumberHelper.toNumber(configProperty['value']);
        }

        protected readConfig(config: api.form.inputtype.InputTypeViewContext): void {
            this.min = this.getConfigProperty(config, 'min');
            this.max = this.getConfigProperty(config, 'max');
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            if (!ValueTypes.DOUBLE.equals(property.getType())) {
                property.convertValueType(ValueTypes.DOUBLE);
            }

            let inputEl = api.ui.text.TextInput.middle(undefined, this.getPropertyValue(property));
            inputEl.setName(this.getInput().getName() + '-' + property.getIndex());

            inputEl.onValueChanged((event: api.ValueChangedEvent) => {
                this.value = ValueTypes.DOUBLE.newValue(event.getNewValue());

                let isValid = this.isValid(event.getNewValue());
                let value = isValid ? this.value : this.newInitialValue();

                this.notifyOccurrenceValueChanged(inputEl, value);
                inputEl.updateValidationStatusOnUserInput(isValid);
            });

            return inputEl;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly?: boolean) {
            let input = <api.ui.text.TextInput> occurrence;

            if (!unchangedOnly || !input.isDirty()) {
                input.setValue(this.getPropertyValue(property));
            }
        }

        resetInputOccurrenceElement(occurrence: api.dom.Element) {
            let input = <api.ui.text.TextInput> occurrence;

            input.resetBaseValues();
        }

        valueBreaksRequiredContract(propertyValue: Value, recording?: api.form.inputtype.InputValidationRecording): boolean {
            let currentValue = this.value || propertyValue;

            if (!this.isValidMin(currentValue.getDouble())) {
                if (recording) {
                    recording.setAdditionalValidationRecord(
                        api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                            `The value cannot be less than ${this.min}`).build());
                }

                return true;
            }

            if (!this.isValidMax(currentValue.getDouble())) {
                if (recording) {
                    recording.setAdditionalValidationRecord(
                        api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                            `The value cannot be greater than ${this.max}`).build());
                }

                return true;
            }

            return propertyValue.isNull() || !propertyValue.getType().equals(ValueTypes.DOUBLE);
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {
            let value = <api.ui.text.TextInput>inputElement;

            return this.isValid(value.getValue());
        }

        private isEmpty(value: string): boolean {
            return api.util.StringHelper.isEmpty(value);
        }

        private isValid(value: string): boolean {
            if (this.isEmpty(value)) {
                return true;
            }

            if (api.util.NumberHelper.isNumber(+value)) {
                return this.isValidMax(api.util.NumberHelper.toNumber(value)) &&
                       this.isValidMin(api.util.NumberHelper.toNumber(value));
            }

            return false;
        }

        private isValidMin(value: number) {
            if (NumberHelper.isNumber(value)) {
                if (NumberHelper.isNumber(this.min)) {
                    return value >= this.min;
                }
            }

            return true;
        }

        private isValidMax(value: number) {
            if (NumberHelper.isNumber(value)) {
                if (NumberHelper.isNumber(this.max)) {
                    return value <= this.max;
                }
            }

            return true;
        }

        protected additionalValidate(recording: api.form.inputtype.InputValidationRecording) {
            //Do nothing
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class('Double', Double));
}
