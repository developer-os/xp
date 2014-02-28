module api.app.wizard {

    export class WizardHeaderWithName extends WizardHeader {

        private nameEl: api.ui.TextInput;

        constructor() {
            super();

            this.nameEl = api.ui.AutosizeTextInput.large().setName('name').setForbiddenCharsRe(/[^a-z0-9\-]+/ig);
            this.nameEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.notifyPropertyChanged("name", event.getOldValue(), event.getNewValue());
            });
            this.appendChild(this.nameEl);

        }

        getName(): string {
            return this.nameEl.getValue();
        }

        setName(value: string) {
            this.nameEl.setValue(value);
        }

        giveFocus(): boolean {
            return this.nameEl.giveFocus();
        }
    }
}