const {Octokit} = require("@octokit/rest");

async function run() {
    const octokit = new Octokit({
        auth: process.env.GITHUB_TOKEN,
    });

    const context = process.env.GITHUB_CONTEXT;
    const {pull_request, repository} = JSON.parse(context);
    const {owner, repo} = repository;
    const prNumber = pull_request.number;
    const prBody = pull_request.body;

    const dependencyRegex = /## Dependencies\s*([\s\S]*)/;
    const match = prBody.match(dependencyRegex);

    if (match) {
        const dependenciesSection = match[1];
        const prLinks = dependenciesSection.match(/- https:\/\/github\.com\/[\w-]+\/[\w-]+\/pull\/\d+/g);

        if (prLinks && prLinks.length > 0) {
            let hasOpenDependencies = false;

            for (const link of prLinks) {
                const [, depOwner, depRepo, depNumber] = link.match(/github\.com\/([\w-]+)\/([\w-]+)\/pull\/(\d+)/);
                const {data: dependencyPr} = await octokit.pulls.get({
                    owner: depOwner,
                    repo: depRepo,
                    pull_number: depNumber,
                });

                if (dependencyPr.state === "open") {
                    hasOpenDependencies = true;
                    break;
                }
            }

            const labels = pull_request.labels.map(label => label.name);

            if (hasOpenDependencies && !labels.includes("Waiting on Dependency PR")) {
                await octokit.issues.addLabels({
                    owner,
                    repo,
                    issue_number: prNumber,
                    labels: ["Waiting on Dependency PR"],
                });
            } else if (!hasOpenDependencies && labels.includes("Waiting on Dependency PR")) {
                await octokit.issues.removeLabel({
                    owner,
                    repo,
                    issue_number: prNumber,
                    name: "Waiting on Dependency PR",
                });
            }
        }
    }
}

run().catch(error => {
    console.error(error);
    process.exit(1);
});
