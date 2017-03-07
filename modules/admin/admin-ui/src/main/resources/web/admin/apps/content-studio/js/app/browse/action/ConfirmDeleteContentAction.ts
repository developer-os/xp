import '../../../api.ts';
import {DeleteContentAction} from './DeleteContentAction';
import {ContentTreeGrid} from '../ContentTreeGrid';

export class ConfirmDeleteContentAction extends DeleteContentAction {

    constructor(grid: ContentTreeGrid) {
        super(grid);
        this.setLabel('Confirm Delete');
        this.setEnabled(true);
        this.setVisible(false);
    }
}
