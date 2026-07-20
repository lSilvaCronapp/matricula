import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';

@Component({
  selector: 'app-matriculas-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatTabsModule],
  templateUrl: './matriculas-shell.component.html',
  styleUrl: './matriculas-shell.component.scss'
})
export class MatriculasShellComponent {
  readonly tabs = [
    { label: 'Nova', route: '/matriculas/nova' },
    { label: 'Por aluno', route: '/matriculas/por-aluno' },
    { label: 'Por turma', route: '/matriculas/por-turma' }
  ];
}
