module app.view {

    export class ContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummary> {

        private previewPanel: ContentItemPreviewPanel;

        private analyticsPanel: ContentItemAnalyticsPanel;

        constructor() {
            super();

            this.previewPanel = new ContentItemPreviewPanel();
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Preview"), this.previewPanel);

            this.analyticsPanel = new ContentItemAnalyticsPanel();
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Google Analytics"), this.analyticsPanel);

            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Details"), new api.ui.Panel());
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Relationships"), new api.ui.Panel());
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Version History"), new api.ui.Panel());
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("SEO"), new api.ui.Panel());

            this.getTabMenu().onNavigationItemSelected((event: api.ui.DeckPanelNavigatorEvent) => {
                this.onTabSelected(event.getTabItem());
            });

            this.showPanel(0);
        }

        setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            super.setItem(item);
            if (this.getTabMenu().getSelectedIndex() == 0) {
                this.previewPanel.setItem(item);
            }
        }

        private onTabSelected(item: api.ui.PanelNavigationItem) {
            if (item.getIndex() == 0) {
                this.getHeader().hide();
                if (this.getBrowseItem()) {
                    this.previewPanel.setItem(this.getBrowseItem());
                }
            } else {
                this.getHeader().show();
            }
        }

    }

}
