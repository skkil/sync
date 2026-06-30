import CreatePostDialog from './_components/CreatePostDialog';
import Posts from './_components/Posts';

export default function ProfilePosts() {
  return (
    <div>
      <div className="flex justify-end mb-4">
        <CreatePostDialog />
      </div>

      <Posts />
    </div>
  );
}
