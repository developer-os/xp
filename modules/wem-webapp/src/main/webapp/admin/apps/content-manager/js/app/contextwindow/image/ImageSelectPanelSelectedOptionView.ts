module app_contextwindow_image {

    export class ImageSelectPanelSelectedOptionView extends api_ui_combobox.SelectedOptionView<api_content.ContentSummary> {

        private content:api_content.ContentSummary;

        constructor(option:api_ui_combobox.Option<api_content.ContentSummary>) {
            this.content = option.displayValue;
            super(option);
        }

        layout() {

            var image = new api_dom.ImgEl();
            image.getEl().setSrc(this.content.getIconUrl());
            image.getEl().setHeight("35px");
            image.getEl().setWidth("35px");

            var label = new api_dom.DivEl(null, "label");
            label.getEl().setInnerHtml(this.content.getName());

            var removeButton = new api_dom.AEl(null, "remove");
            removeButton.setText("&times;");
            removeButton.getEl().addEventListener('click', (event:Event) => {
                this.notifySelectedOptionToBeRemoved();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(image);
            this.appendChild(label);
            this.appendChild(removeButton);

        }
    }
}