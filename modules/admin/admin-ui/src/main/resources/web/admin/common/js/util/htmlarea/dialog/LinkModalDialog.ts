module api.util.htmlarea.dialog {

    import Form = api.ui.form.Form;
    import FormItem = api.ui.form.FormItem;
    import Panel = api.ui.panel.Panel;
    import DockedPanel = api.ui.panel.DockedPanel;
    import Validators = api.ui.form.Validators;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import Option = api.ui.selector.Option;
    import InputAlignment = api.ui.InputAlignment;

    export class LinkModalDialog extends ModalDialog {
        private dockedPanel: DockedPanel;
        private link: HTMLElement;
        private linkText: string;
        private onlyTextSelected: boolean;

        private content: api.content.ContentSummary;

        private static tabNames: any = {
            content: "Content",
            url: "URL",
            download: "Download",
            email: "Email",
            anchor: "Anchor"
        };

        private static contentPrefix = "content://";
        private static downloadPrefix = "media://download/";
        private static emailPrefix = "mailto:";
        private static subjectPrefix = "?subject=";

        constructor(config: HtmlAreaAnchor, content: api.content.ContentSummary) {
            super(config.editor, "Insert Link", "link-modal-dialog");

            this.link = config.element;
            this.linkText = config.text;

            this.content = content;

            if (config.anchorList.length > 0) {
                this.dockedPanel.addItem(LinkModalDialog.tabNames.anchor, true, this.createAnchorPanel(config.anchorList), this.isAnchor());
            }

            this.onlyTextSelected = config.onlyTextSelected
            if (this.onlyTextSelected) {
                let formItem = this.createFormItem("linkText", "Text", Validators.required, this.getLinkText());
                this.getMainForm().insertChild(this.createFieldSet(formItem), 0);
                formItem.getInput().giveFocus();
            }
        }

        private getHref(): string {
            return this.link ? this.link.getAttribute("href") : api.util.StringHelper.EMPTY_STRING;
        }

        private getLinkText(): string {
            return this.link ? this.link["text"] : this.linkText;
        }

        private getToolTip(): string {
            return this.link ? this.link.getAttribute("title") : api.util.StringHelper.EMPTY_STRING;
        }

        private isContentLink(): boolean {
            return this.getHref().indexOf(LinkModalDialog.contentPrefix) === 0;
        }

        private getContentId(): string {
            if (this.link && this.isContentLink()) {
                return this.getHref().replace(LinkModalDialog.contentPrefix, api.util.StringHelper.EMPTY_STRING);
            }
            return api.util.StringHelper.EMPTY_STRING;
        }

        private isDownloadLink(): boolean {
            return this.getHref().indexOf(LinkModalDialog.downloadPrefix) === 0;
        }

        private getDownloadId(): string {
            return this.isDownloadLink() ?
                   this.getHref().replace(LinkModalDialog.downloadPrefix, api.util.StringHelper.EMPTY_STRING) :
                   api.util.StringHelper.EMPTY_STRING;
        }

        private isUrl(): boolean {
            return this.link ? !(this.isContentLink() || this.isDownloadLink() || this.isEmail()) : false;
        }

        private getUrl(): string {
            return this.isUrl() ? this.getHref() : api.util.StringHelper.EMPTY_STRING;
        }

        private isEmail(): boolean {
            return this.getHref().indexOf(LinkModalDialog.emailPrefix) === 0;
        }

        private getEmail(): string {
            if (!this.isEmail()) {
                return api.util.StringHelper.EMPTY_STRING;
            }
            var emailArr = this.getHref().split(LinkModalDialog.subjectPrefix);
            return emailArr[0].replace(LinkModalDialog.emailPrefix, api.util.StringHelper.EMPTY_STRING);
        }

        private isAnchor(): boolean {
            return this.getHref().indexOf("#") === 0;
        }

        private getAnchor(): string {
            return this.isAnchor() ? this.getHref() : api.util.StringHelper.EMPTY_STRING;
        }

        private getSubject(): string {
            if (!this.isEmail() || this.getHref().indexOf(LinkModalDialog.subjectPrefix) == -1) {
                return api.util.StringHelper.EMPTY_STRING;
            }
            var emailArr = this.getHref().split(LinkModalDialog.subjectPrefix);
            return decodeURI(emailArr[1].replace(LinkModalDialog.subjectPrefix, api.util.StringHelper.EMPTY_STRING));
        }

        protected layout() {
            super.layout();
            this.appendChildToContentPanel(this.dockedPanel = this.createDockedPanel());

            this.getMainForm().onValidityChanged(() => {
                this.centerMyself();
            });

            this.dockedPanel.getDeck().onPanelShown(() => {
                this.centerMyself();
            })

            this.dockedPanel.onRendered(() => {
                this.centerMyself();
            })

        }

        private createContentPanel(): Panel {
            return this.createFormPanel([
                this.createContentSelector("contentId", "Target", this.getContentId),
                this.createTargetCheckbox("contentTarget", this.isContentLink)
            ]);
        }

        private createDownloadPanel(): Panel {
            return this.createFormPanel([
                this.createContentSelector("downloadId", "Target", this.getDownloadId, api.schema.content.ContentTypeName.getMediaTypes())
            ]);
        }

        private createUrlPanel(): Panel {
            return this.createFormPanel([
                this.createFormItemWithPostponedValue("url", "Url", this.getUrl, Validators.required),
                this.createTargetCheckbox("urlTarget", this.isUrl)
            ]);
        }

        private createAnchorPanel(anchorList: string[]): Panel {
            return this.createFormPanel([
                this.createAnchorDropdown(anchorList)
            ]);
        }

        private createEmailPanel(): Panel {
            var emailFormItem: FormItem = this.createFormItemWithPostponedValue("email", "Email", this.getEmail,
                LinkModalDialog.validationRequiredEmail);

            emailFormItem.getLabel().addClass("required");

            return this.createFormPanel([
                emailFormItem,
                this.createFormItemWithPostponedValue("subject", "Subject", this.getSubject)
            ]);
        }

        private static validationRequiredEmail(input: api.dom.FormInputEl): string {
            return Validators.required(input) || Validators.validEmail(input);
        }

        private getTarget(isTabSelected: boolean): boolean {
            return isTabSelected ? !api.util.StringHelper.isBlank(this.link.getAttribute("target")) : false;
        }

        private createTargetCheckbox(id: string, isTabSelectedFn: Function): FormItem {
            var checkbox = api.ui.Checkbox.create().setLabelText("Open in new tab").setInputAlignment(InputAlignment.RIGHT).build();

            this.onAdded(() => {
                checkbox.setChecked(this.getTarget(isTabSelectedFn.call(this)));
            });

            return this.createFormItem(id, null, null, null, checkbox);
        }

        protected getMainFormItems(): FormItem [] {
            let toolTipFormItem = this.createFormItemWithPostponedValue("toolTip", "Tooltip", this.getToolTip);

            return [toolTipFormItem];
        }

        private createDockedPanel(): DockedPanel {
            var dockedPanel = new DockedPanel();
            dockedPanel.addItem(LinkModalDialog.tabNames.content, true, this.createContentPanel());
            dockedPanel.addItem(LinkModalDialog.tabNames.url, true, this.createUrlPanel());
            dockedPanel.addItem(LinkModalDialog.tabNames.download, true, this.createDownloadPanel());
            dockedPanel.addItem(LinkModalDialog.tabNames.email, true, this.createEmailPanel());

            this.onAdded(() => {
                dockedPanel.getDeck().getPanels().forEach((panel, index) => {
                    if ((index == 1 && this.isUrl()) || (index == 2 && this.isDownloadLink()) || (index == 3 && this.isEmail()) ||
                        (index == 4 && this.isAnchor())) {
                        dockedPanel.selectPanel(panel);
                        return false;
                    }
                });
            });

            dockedPanel.getDeck().getPanels().forEach((panel) => {
                (<Form>panel.getFirstChild()).onValidityChanged(() => {
                    this.centerMyself();
                })
            });

            return dockedPanel;
        }

        protected initializeActions() {
            var submitAction = new api.ui.Action(this.link ? "Update" : "Insert");
            this.setSubmitAction(submitAction);

            this.addAction(submitAction.onExecuted(() => {
                if (this.validate()) {
                    this.createLink();
                    this.close();
                }
            }));

            super.initializeActions();
        }

        private createContentSelector(id: string, label: string, getValueFn: Function,
                                      contentTypeNames?: api.schema.content.ContentTypeName[]): FormItem {
            let loader = new api.content.resource.ContentSummaryLoader();
            loader.onLoadingData((event) => {
                loader.setContentPath(this.content.getPath());
            });

            let contentSelector = api.content.ContentComboBox.create().setLoader(loader).setMaximumOccurrences(1).build(),
                contentSelectorComboBox = contentSelector.getComboBox();

            contentSelectorComboBox.onValueChanged(() => {
                this.centerMyself();
            });

            if (contentTypeNames) {
                loader.setAllowedContentTypeNames(contentTypeNames);
            }

            contentSelectorComboBox.onKeyDown((e: KeyboardEvent) => {
                if (api.ui.KeyHelper.isEscKey(e) && !contentSelectorComboBox.isDropdownShown()) {
                    // Prevent modal dialog from closing on Esc key when dropdown is expanded
                    e.preventDefault();
                    e.stopPropagation();
                }
            });

            this.onAdded(() => {
                contentSelector.setValue(getValueFn.call(this));
            });

            return this.createFormItem(id, label, Validators.required, null, <api.dom.FormItemEl>contentSelector);
        }

        private createAnchorDropdown(anchorList: string[]): FormItem {
            var dropDown = new Dropdown<string>("anchor", <DropdownConfig<string>>{});

            anchorList.forEach((anchor: string) => {
                dropDown.addOption(<Option<string>>{value: "#" + anchor, displayValue: anchor});
            });

            if (this.getAnchor()) {
                dropDown.setValue(this.getAnchor());
            }

            return this.createFormItem("anchor", "Anchor", Validators.required, null, <api.dom.FormItemEl>dropDown);
        }

        private validateDockPanel(): boolean {
            var form = <Form>this.dockedPanel.getDeck().getPanelShown().getFirstChild();

            return form.validate(true).isValid();
        }

        protected validate(): boolean {
            var mainFormValid = super.validate();
            var dockPanelValid = this.validateDockPanel();

            return mainFormValid && dockPanelValid;
        }

        private createContentLink(): api.dom.AEl {
            var contentSelector = <api.content.ContentComboBox>this.getFieldById("contentId"),
                targetCheckbox = <api.ui.Checkbox>this.getFieldById("contentTarget");

            var linkEl = new api.dom.AEl();
            linkEl.setUrl(LinkModalDialog.contentPrefix + contentSelector.getValue(), targetCheckbox.isChecked() ? "_blank" : null);

            return linkEl;
        }

        private createDownloadLink(): api.dom.AEl {
            var contentSelector = <api.content.ContentComboBox>this.getFieldById("downloadId");

            var linkEl = new api.dom.AEl();
            linkEl.setUrl(LinkModalDialog.downloadPrefix + contentSelector.getValue());

            return linkEl;
        }

        private createUrlLink(): api.dom.AEl {
            var url = (<api.ui.text.TextInput>this.getFieldById("url")).getValue(),
                targetCheckbox = <api.ui.Checkbox>this.getFieldById("urlTarget");

            var linkEl = new api.dom.AEl();
            linkEl.setUrl(url, targetCheckbox.isChecked() ? "_blank" : null);

            return linkEl;
        }

        private createAnchor(): api.dom.AEl {
            var anchorName = (<api.ui.text.TextInput>this.getFieldById("anchor")).getValue();

            var linkEl = new api.dom.AEl();
            linkEl.setUrl(anchorName);

            return linkEl;
        }

        private createEmailLink(): api.dom.AEl {
            var email = (<api.ui.text.TextInput>this.getFieldById("email")).getValue(),
                subject = (<api.ui.text.TextInput>this.getFieldById("subject")).getValue();

            var linkEl = new api.dom.AEl();
            linkEl.setUrl(LinkModalDialog.emailPrefix + email + (subject ? LinkModalDialog.subjectPrefix + encodeURI(subject) : ""));

            return linkEl;
        }

        private createLink(): void {
            var linkEl: api.dom.AEl,
                deck = <api.ui.panel.NavigatedDeckPanel>this.dockedPanel.getDeck(),
                selectedTab = <api.ui.tab.TabBarItem>deck.getSelectedNavigationItem(),
                linkText: string = this.onlyTextSelected ? (<api.ui.text.TextInput>this.getFieldById("linkText")).getValue() : "",
                toolTip: string = (<api.ui.text.TextInput>this.getFieldById("toolTip")).getValue();

            switch (selectedTab.getLabel()) {
            case (LinkModalDialog.tabNames.content):
                linkEl = this.createContentLink();
                break;
            case (LinkModalDialog.tabNames.url):
                linkEl = this.createUrlLink();
                break;
            case (LinkModalDialog.tabNames.download):
                linkEl = this.createDownloadLink();
                break;
            case (LinkModalDialog.tabNames.email):
                linkEl = this.createEmailLink();
                break;
            case (LinkModalDialog.tabNames.anchor):
                linkEl = this.createAnchor();
                break;
            }

            linkEl.setHtml(linkText);
            if (toolTip) {
                linkEl.setTitle(toolTip);
            }

            if (this.link) {
                this.link.parentElement.replaceChild(linkEl.getHTMLElement(), this.link);
            }
            else {
                if (this.onlyTextSelected) {
                    this.getEditor().insertContent(linkEl.toString());
                }
                else {
                    var linkAttrs = {
                        href: linkEl.getHref(),
                        target: linkEl.getTarget() ? linkEl.getTarget() : null,
                        rel: null,
                        "class": null,
                        title: linkEl.getTitle()
                    };

                    this.getEditor().execCommand('mceInsertLink', false, linkAttrs);
                }
            }
        }

        protected createFormItemWithPostponedValue(id: string, label: string, getValueFn: Function,
                                                   validator?: (input: api.dom.FormInputEl) => string): FormItem {

            let formItem = super.createFormItem(id, label, validator);

            this.onAdded(() => {
                (<api.dom.InputEl>formItem.getInput()).setValue(getValueFn.call(this));
            });

            return formItem;
        }
    }

}
