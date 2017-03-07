import '../../api.ts';
import Action = api.ui.Action;

export class ContentDeleteActionButton extends api.ui.button.ActionButton {

    constructor(action: Action) {
        super(action, false);

        this.addClass('content-delete-button');
        this.setVisible(false);
    }
}
