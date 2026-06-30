import { StateCreator } from 'zustand';

export interface Project {
  handle: string;
  name: string;
}

export interface ProjectState {
  currentProject: Project | null;
}

export interface ProjectActions {
  setCurrentProject: (project: Project) => void;
  clearCurrentProject: () => void;
}

export type ProjectSlice = ProjectState & ProjectActions;

const initialState: ProjectState = {
  currentProject: null,
};

export const createProjectSlice: StateCreator<
  ProjectSlice,
  [],
  [],
  ProjectSlice
> = (set) => ({
  ...initialState,
  setCurrentProject: (project) => set({ currentProject: project }),
  clearCurrentProject: () => set({ currentProject: null }),
});
