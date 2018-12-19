/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ArenaTestModule } from '../../../test.module';
import { CompetitionProblemDeleteDialogComponent } from 'app/entities/competition-problem/competition-problem-delete-dialog.component';
import { CompetitionProblemService } from 'app/entities/competition-problem/competition-problem.service';

describe('Component Tests', () => {
    describe('CompetitionProblem Management Delete Component', () => {
        let comp: CompetitionProblemDeleteDialogComponent;
        let fixture: ComponentFixture<CompetitionProblemDeleteDialogComponent>;
        let service: CompetitionProblemService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ArenaTestModule],
                declarations: [CompetitionProblemDeleteDialogComponent]
            })
                .overrideTemplate(CompetitionProblemDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CompetitionProblemDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CompetitionProblemService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
