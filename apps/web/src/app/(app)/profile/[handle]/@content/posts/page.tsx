import CreateReflectionDialog from './_components/CreateReflectionDialog';
import Reflections from './_components/Reflections';

export default function ProfilePosts() {
  return (
    <div>
      <div className="flex justify-end mb-4">
        <CreateReflectionDialog />
      </div>

      <Reflections />
    </div>
  );
}
