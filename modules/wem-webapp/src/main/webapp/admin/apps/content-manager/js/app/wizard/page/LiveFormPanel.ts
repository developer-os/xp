module app.wizard {
    export class LiveFormPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;
        private baseUrl: string;
        private url: string;
        private site: api.content.Content;
        private contextWindow: app.contextwindow.ContextWindow;
        private contentWizardPanel: ContentWizardPanel;

        constructor(site: api.content.Content, contentWizardPanel: ContentWizardPanel) {
            super("live-form-panel");
            this.baseUrl = api.util.getUri("portal/edit/");
            this.site = site;
            this.contentWizardPanel = contentWizardPanel;

            this.frame = new api.dom.IFrameEl();
            this.frame.addClass("live-edit-frame");
            this.appendChild(this.frame);

            this.contextWindow = new app.contextwindow.ContextWindow({
                liveEditEl: this.frame,
                site: this.site,
                liveFormPanel: this
            });
        }

        private doLoad(liveEditUrl: string): Q.Promise<void> {

            console.log("LiveFormPanel.doLoad() ... url: " + liveEditUrl);

            var deferred = Q.defer<void>();

            this.frame.setSrc(liveEditUrl);

            // Wait for iframe to be loaded before adding context window!
            var maxIterations = 100;
            var iterations = 0;
            var contextWindowAdded = false;
            var intervalId = setInterval(() => {

                if (this.frame.isLoaded()) {
                    var contextWindowElement = this.frame.getHTMLElement()["contentWindow"];
                    if (contextWindowElement && contextWindowElement.$liveEdit) {

                        this.appendChild(this.contextWindow);
                        contextWindowAdded = true;
                        //contextWindow.init();
                        clearInterval(intervalId);

                        console.log("LiveFormPanel.doLoad() ... Live edit loaded");

                        deferred.resolve(null);
                    }
                }

                iterations++;
                if (iterations >= maxIterations) {
                    clearInterval(intervalId);
                    if (contextWindowAdded) {
                        deferred.resolve(null);
                    }
                    else {
                        deferred.reject(null);
                    }
                }
            }, 200);

            return deferred.promise;
        }

        renderExisting(content: api.content.Content, pageTemplate: api.content.page.PageTemplate) {

            console.log("LiveFormPanel.renderExisting() ...");

            if (content.isPage() && pageTemplate != null) {

                var liveEditUrl = this.baseUrl + content.getContentId().toString();

                this.doLoad(liveEditUrl).
                    then(() => {
                        console.log("LiveFormPanel.renderExisting() calling contextWindow.setPage ");
                        this.contextWindow.setPage(content, pageTemplate);
                    }).fail(()=> {
                        console.log("LiveFormPanel.renderExisting() loading Live edit failed (time out)");
                    });
            }
        }

        saveChanges() {
            this.contentWizardPanel.saveChanges();
        }

        public getRegions(): api.content.page.PageRegions {

            return this.contextWindow.getPageRegions();

//            var pageRegions = new api.content.page.PageRegionsBuilder();
//
//            // Header region
//            var headerRegion = new api.content.page.region.RegionBuilder();
//            headerRegion.setName("header");
//            var partInHeader = new api.content.page.part.PartComponentBuilder().
//                setName(new api.content.page.ComponentName("PartInHeader")).
//                setTemplate(api.content.page.part.PartTemplateKey.fromString("Blueman-1.0.0|demo-1.0.0|my-part")).
//                build();
//            headerRegion.addComponent(partInHeader);
//            pageRegions.addRegion(headerRegion.build());
//
//            // Main region
//            var mainRegion = new api.content.page.region.RegionBuilder();
//            mainRegion.setName("main");
//            var fancyImage = new api.content.page.image.ImageComponentBuilder().
//                setImage(new api.content.ContentId("123")).
//                setName(new api.content.page.ComponentName("FancyImage")).
//                setTemplate(api.content.page.image.ImageTemplateKey.fromString("Blueman-1.0.0|demo-1.0.0|fancy-image")).
//                build();
//            mainRegion.addComponent(fancyImage);
//            pageRegions.addRegion(mainRegion.build());
//
//            // Footer region
//            var footerRegion = new api.content.page.region.RegionBuilder();
//            footerRegion.setName("footer");
//
//            var twoColumnsLeftRegion = new api.content.page.region.RegionBuilder().
//                setName("twoColumnsLeftRegion").
//                addComponent(new api.content.page.part.PartComponentBuilder().
//                    setName(new api.content.page.ComponentName("PartInTwoColumnLeft")).
//                    setTemplate(api.content.page.part.PartTemplateKey.fromString("Blueman-1.0.0|demo-1.0.0|my-part")).
//                    build()).
//                build();
//            var twoColumnsRightRegion = new api.content.page.region.RegionBuilder().
//                setName("twoColumnsRightRegion").
//                addComponent(new api.content.page.part.PartComponentBuilder().
//                    setName(new api.content.page.ComponentName("PartInTwoColumnRight")).
//                    setTemplate(api.content.page.part.PartTemplateKey.fromString("Blueman-1.0.0|demo-1.0.0|my-part")).
//                    build()).
//                build();
//            var twoColumnsRegions = new api.content.page.layout.LayoutRegionsBuilder().
//                addRegion(twoColumnsLeftRegion).
//                addRegion(twoColumnsRightRegion).
//                build();
//            var twoColumns = new api.content.page.layout.LayoutComponentBuilder().
//                setRegions(twoColumnsRegions).
//                setName(new api.content.page.ComponentName("FooterTwoColumns")).
//                setTemplate(api.content.page.layout.LayoutTemplateKey.fromString("Blueman-1.0.0|demo-1.0.0|two-columns")).
//                build();
//            footerRegion.addComponent(twoColumns);
//
//            pageRegions.addRegion(footerRegion.build());
//
//            return pageRegions.build();
        }
    }
}