import { Routes } from '@angular/router';
import { ShellLayoutComponent } from './layout/shell-layout.component';
import { AlunoListComponent } from './features/alunos/aluno-list.component';
import { AlunoFormComponent } from './features/alunos/aluno-form.component';
import { CursoListComponent } from './features/cursos/curso-list.component';
import { CursoFormComponent } from './features/cursos/curso-form.component';
import { DisciplinaListComponent } from './features/disciplinas/disciplina-list.component';
import { DisciplinaFormComponent } from './features/disciplinas/disciplina-form.component';
import { TurmaListComponent } from './features/turmas/turma-list.component';
import { TurmaFormComponent } from './features/turmas/turma-form.component';
import { MatriculasShellComponent } from './features/matriculas/matriculas-shell.component';
import { MatriculaNovaComponent } from './features/matriculas/matricula-nova.component';
import { MatriculaPorAlunoComponent } from './features/matriculas/matricula-por-aluno.component';
import { MatriculaPorTurmaComponent } from './features/matriculas/matricula-por-turma.component';

export const routes: Routes = [
  {
    path: '',
    component: ShellLayoutComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'matriculas' },
      {
        path: 'matriculas',
        component: MatriculasShellComponent,
        children: [
          { path: '', pathMatch: 'full', redirectTo: 'nova' },
          { path: 'nova', component: MatriculaNovaComponent },
          { path: 'por-aluno', component: MatriculaPorAlunoComponent },
          { path: 'por-turma', component: MatriculaPorTurmaComponent }
        ]
      },
      { path: 'alunos', component: AlunoListComponent },
      { path: 'alunos/novo', component: AlunoFormComponent },
      { path: 'alunos/:id/editar', component: AlunoFormComponent },
      { path: 'cursos', component: CursoListComponent },
      { path: 'cursos/novo', component: CursoFormComponent },
      { path: 'cursos/:id/editar', component: CursoFormComponent },
      { path: 'disciplinas', component: DisciplinaListComponent },
      { path: 'disciplinas/novo', component: DisciplinaFormComponent },
      { path: 'disciplinas/:id/editar', component: DisciplinaFormComponent },
      { path: 'turmas', component: TurmaListComponent },
      { path: 'turmas/novo', component: TurmaFormComponent },
      { path: 'turmas/:id/editar', component: TurmaFormComponent }
    ]
  },
  { path: '**', redirectTo: 'matriculas' }
];
