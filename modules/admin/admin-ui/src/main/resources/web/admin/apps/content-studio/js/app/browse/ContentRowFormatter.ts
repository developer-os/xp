import "../../api.ts";
import TreeNode = api.ui.treegrid.TreeNode;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;
import PublishStatus = api.content.PublishStatus;

export class ContentRowFormatter {

    public static nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
        const data = node.getData();
        if (data.getContentSummary() || data.getUploadItem()) {
            let viewer = <ContentSummaryAndCompareStatusViewer> node.getViewer("name");
            if (!viewer) {
                viewer = new ContentSummaryAndCompareStatusViewer();
                node.setViewer("name", viewer);
            }
            viewer.setObject(node.getData(), node.calcLevel() > 1);
            return viewer ? viewer.toString() : "";
        }

        return "";
    }

    public static orderFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
        var wrapper = new api.dom.SpanEl();

        if (!api.util.StringHelper.isBlank(value)) {
            wrapper.getEl().setTitle(value);
        }

        if (node.getData().getContentSummary()) {
            var childOrder = node.getData().getContentSummary().getChildOrder();
            var icon;
            if (!childOrder.isDefault()) {
                if (!childOrder.isManual()) {
                    if (childOrder.isDesc()) {
                        icon = new api.dom.DivEl("icon-arrow-up2 sort-dialog-trigger");
                    } else {
                        icon = new api.dom.DivEl("icon-arrow-down4 sort-dialog-trigger");
                    }
                } else {
                    icon = new api.dom.DivEl(api.StyleHelper.getCommonIconCls("menu") + " sort-dialog-trigger");
                }
                wrapper.getEl().setInnerHtml(icon.toString(), false);
            }
        }
        return wrapper.toString();
    }

    public static statusFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {

        var data = node.getData(),
            compareStatusText;

        if (!!data.getContentSummary()) {   // default node
            var compareStatus: CompareStatus = CompareStatus[CompareStatus[value]],
                publishStatus: PublishStatus = data.getPublishStatus();

            compareStatusText = api.content.CompareStatusFormatter.formatStatus(compareStatus);

            if (PublishStatus[publishStatus] && (publishStatus == PublishStatus.PENDING || publishStatus == PublishStatus.EXPIRED)) {
                var statusEl = new api.dom.DivEl(ContentRowFormatter.makeClassName(CompareStatus[value]));
                statusEl.getEl().setText(compareStatusText);
                statusEl.addClass(ContentRowFormatter.makeClassName(PublishStatus[publishStatus]));

                var publishStatusEl = new api.dom.DivEl(),
                    publishStatusText = api.content.PublishStatusFormatter.formatStatus(publishStatus);

                publishStatusEl.getEl().setText('(' + publishStatusText + ')');
                publishStatusEl.addClass(ContentRowFormatter.makeClassName(CompareStatus[value]));
                publishStatusEl.addClass(ContentRowFormatter.makeClassName(PublishStatus[publishStatus]));

                return statusEl.toString() + publishStatusEl.toString();
            } else {
                var statusEl = new api.dom.SpanEl();
                if (CompareStatus[value]) {
                    statusEl.addClass(ContentRowFormatter.makeClassName(CompareStatus[value]));
                }
                statusEl.getEl().setText(compareStatusText);
                return statusEl.toString();
            }
        } else if (!!data.getUploadItem()) { // uploading node
            compareStatusText = new api.ui.ProgressBar(data.getUploadItem().getProgress());
            return new api.dom.SpanEl().appendChild(compareStatusText).toString();
        }
    }

    private static makeClassName(entry: string): string {
        return entry.toLowerCase().replace("_", "-") || "unknown";
    }
}
