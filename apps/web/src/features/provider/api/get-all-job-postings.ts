import { useQuery } from '@tanstack/react-query';

export interface JobPosting {
  id: string;
  jobTitle: string;
  jobDescription: string;
  location: string;
  createdAt: string;
  companyId: string;
  companyName: string;
  companyLogo?: string;
}

// Mock data - replace with actual API call when backend endpoint is ready
const mockJobPostings: JobPosting[] = [
  {
    id: '1',
    jobTitle: 'Senior Frontend Developer',
    jobDescription:
      'We are looking for an experienced Frontend Developer to join our team. You will be responsible for building modern web applications using React, TypeScript, and Next.js.\n\nResponsibilities:\n- Develop and maintain frontend applications\n- Collaborate with designers and backend developers\n- Write clean, maintainable code\n- Participate in code reviews\n\nRequirements:\n- 5+ years of experience with React\n- Strong TypeScript skills\n- Experience with Next.js\n- Excellent communication skills',
    location: 'San Francisco, CA',
    createdAt: new Date('2024-03-20').toISOString(),
    companyId: 'company-1',
    companyName: 'Tech Innovations Inc.',
  },
  {
    id: '2',
    jobTitle: 'Full Stack Engineer',
    jobDescription:
      'Join our growing team as a Full Stack Engineer. Work on exciting projects using modern technologies.\n\nResponsibilities:\n- Build and maintain full-stack applications\n- Work with React, Node.js, and PostgreSQL\n- Design and implement APIs\n- Optimize application performance\n\nRequirements:\n- 3+ years of full-stack development experience\n- Proficiency in React and Node.js\n- Database design experience\n- Strong problem-solving skills',
    location: 'Remote',
    createdAt: new Date('2024-03-18').toISOString(),
    companyId: 'company-2',
    companyName: 'StartUp Labs',
  },
  {
    id: '3',
    jobTitle: 'UI/UX Designer',
    jobDescription:
      'We are seeking a talented UI/UX Designer to create beautiful and intuitive user experiences.\n\nResponsibilities:\n- Design user interfaces for web and mobile applications\n- Create wireframes, prototypes, and mockups\n- Conduct user research and testing\n- Collaborate with developers and product managers\n\nRequirements:\n- 4+ years of UI/UX design experience\n- Proficiency in Figma and Adobe Creative Suite\n- Strong portfolio demonstrating design skills\n- Understanding of frontend technologies',
    location: 'New York, NY',
    createdAt: new Date('2024-03-15').toISOString(),
    companyId: 'company-3',
    companyName: 'Design Studio Co.',
  },
  {
    id: '4',
    jobTitle: 'Backend Developer',
    jobDescription:
      'Looking for a Backend Developer to build scalable APIs and services.\n\nResponsibilities:\n- Develop RESTful APIs\n- Design database schemas\n- Implement authentication and authorization\n- Optimize system performance\n\nRequirements:\n- 3+ years of backend development experience\n- Strong knowledge of Node.js or Python\n- Experience with PostgreSQL or MongoDB\n- Understanding of cloud platforms (AWS, GCP)',
    location: 'Austin, TX',
    createdAt: new Date('2024-03-10').toISOString(),
    companyId: 'company-4',
    companyName: 'Cloud Systems Ltd.',
  },
  {
    id: '5',
    jobTitle: 'DevOps Engineer',
    jobDescription:
      'Join our infrastructure team to build and maintain cloud infrastructure.\n\nResponsibilities:\n- Manage CI/CD pipelines\n- Deploy and monitor applications\n- Implement infrastructure as code\n- Ensure system reliability and security\n\nRequirements:\n- 4+ years of DevOps experience\n- Expertise in Docker and Kubernetes\n- Experience with AWS or Azure\n- Strong scripting skills (Bash, Python)',
    location: 'Seattle, WA',
    createdAt: new Date('2024-03-05').toISOString(),
    companyId: 'company-5',
    companyName: 'Infrastructure Pro',
  },
  {
    id: '6',
    jobTitle: 'Product Manager',
    jobDescription:
      'We are looking for a Product Manager to drive product strategy and execution.\n\nResponsibilities:\n- Define product roadmap and strategy\n- Gather and prioritize requirements\n- Work closely with engineering and design teams\n- Analyze product metrics and user feedback\n\nRequirements:\n- 5+ years of product management experience\n- Strong analytical and communication skills\n- Experience with agile methodologies\n- Technical background preferred',
    location: 'Boston, MA',
    createdAt: new Date('2024-03-01').toISOString(),
    companyId: 'company-6',
    companyName: 'Product Solutions Inc.',
  },
];

async function getAllJobPostings() {
  // Simulate API delay
  await new Promise((resolve) => setTimeout(resolve, 500));

  // TODO: Replace with actual API call
  // return server.get<{ postings: JobPosting[] }>('jobs').json().then((data) => data.postings);

  return mockJobPostings;
}

export function useGetAllJobPostingsQuery() {
  return useQuery({
    queryKey: ['all-job-postings'],
    queryFn: getAllJobPostings,
  });
}
