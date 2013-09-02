module LiveEdit.component.listener {

    // Uses
    var $ = $liveEdit;

    export class Layout extends LiveEdit.component.listener.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.Configuration[LiveEdit.component.Type.LAYOUT].cssSelector;

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}