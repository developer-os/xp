module app.browse {

    export class ContentBrowseToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: ContentBrowseActions) {
            super();

            this.addAction(actions.SHOW_NEW_CONTENT_DIALOG_ACTION);
            this.addAction(actions.EDIT_CONTENT);
            this.addAction(actions.OPEN_CONTENT);
            this.addAction(actions.DELETE_CONTENT);
            this.addAction(actions.DUPLICATE_CONTENT);
            this.addAction(actions.MOVE_CONTENT);
            this.addGreedySpacer();

            var previewDetailsToggler = new api.ui.ToggleSlide({
                turnOnAction: actions.SHOW_PREVIEW,
                turnOffAction: actions.SHOW_DETAILS
            }, false);
            previewDetailsToggler.setEnabled(actions.SHOW_PREVIEW.isEnabled());
            actions.SHOW_PREVIEW.addPropertyChangeListener((action: api.ui.Action) => {
                previewDetailsToggler.setEnabled(action.isEnabled());
                if (!action.isEnabled() && previewDetailsToggler.isTurnedOn()) {
                    previewDetailsToggler.turnOff();
                }
            });
            super.addElement(previewDetailsToggler);
        }
    }
}
